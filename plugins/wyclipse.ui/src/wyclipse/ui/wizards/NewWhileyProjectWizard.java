// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyclipse.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.w3c.dom.Document;

import wyclipse.core.Activator;
import wyclipse.core.builder.WhileyPath;

/**
 * Responsible for managing the creation of a new Whiley project. The wizard is
 * made up from a number of different pages, each of which has a specific role
 * (e.g. defining the name, the whileypath, etc).
 * 
 * @author David J. Pearce
 * 
 */
public class NewWhileyProjectWizard extends Wizard implements IExecutableExtension, INewWizard {
	private IWorkbench workbench;
	
	private IConfigurationElement config;
	
	private NewWhileyProjectPageOne page1;
	private NewWhileyProjectPageTwo page2;	
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
	}

	@Override
	public void addPages() {
		page1 = new NewWhileyProjectPageOne();
		addPage(page1);
		page2 = new NewWhileyProjectPageTwo();
		addPage(page2);
	}

	public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        this.config = config;
    }
	
	@Override
	public boolean performFinish() {
		
		final IProject projectHandle = page1.getProjectHandle();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// Configure project location
		URI projectURI = (!page1.useDefaults()) ? page1.getLocationURI() : null;

		final IProjectDescription desc = workspace
				.newProjectDescription(projectHandle.getName());

		desc.setLocationURI(projectURI);

		// Create project
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("Creating Whiley Project", 3000);
					createProject(desc, projectHandle, monitor);
					monitor.worked(1000);
					WhileyPath whileypath = page2.getWhileyPath();
					createWhileyPathFile(whileypath,projectHandle,monitor);
					monitor.worked(1000);
					page2.instantiateWhileyPath(projectHandle,monitor);
				} finally {
					monitor.done();
				}
				
			}
		};

		try {
			getContainer().run(true, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException
					.getMessage());
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(config);
        BasicNewProjectResourceWizard.selectAndReveal(projectHandle, workbench
                .getActiveWorkbenchWindow());

        return true;
	}	
	
	/**
	 * Responsible for actually creating the given project and populating its
	 * description. One of the responsibilities of this function is to configure
	 * the project description with the appropriate Whiley nature and builder.
	 * 
	 * @param description
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	void createProject(IProjectDescription description, IProject project,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {

		// First, add Whiley Nature onto list of natures
		String[] natures = new String[] { Activator.WYCLIPSE_NATURE_ID };
		description.setNatureIds(natures);

		// Second, add Whiley Builder onto list of builders
		ICommand buildCommand = description.newCommand();				
		buildCommand.setBuilderName(Activator.WYCLIPSE_BUILDER_ID);

		ICommand[] newBuilders = new ICommand[1];
		newBuilders[0] = buildCommand;
		description.setBuildSpec(newBuilders);		

		// done --- create project
		project.create(description, new SubProgressMonitor(monitor, 1000));

		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}

		project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(
				monitor, 1000));
	}
	
	/**
	 * Create the ".whileypath" file in the project's root.
	 * 
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	void createWhileyPathFile(WhileyPath whileyPath, IProject project, IProgressMonitor monitor)
			throws CoreException {
		
		// ==================================================
		// First, turn the whileypath into an XML bytestream.
		// ==================================================
		Document xmldoc = whileyPath.toXmlDocument();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			DOMSource source = new DOMSource(xmldoc);
			StreamResult result = new StreamResult(bout);
			Transformer transformer = transformerFactory.newTransformer();
			// The following two seamingly random lines ensure that the
			// resulting XML is properly indented, and looks nice. Thanks Stack
			// Overflow!
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// Finally, generate the XML byte stream...
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// FIXME: throw a CoreException?
		} catch (TransformerException e) {
			// FIXME: throw a CoreException?
		}

		// ==================================================
		// Second, create the physical .whileypath file
		// ==================================================
		IFile whileypath = project.getFile(".whileypath");
		ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());

		if (whileypath.exists()) {
			whileypath.setContents(bin, IResource.NONE, monitor);
		} else {
			whileypath.create(bin, IResource.NONE, monitor);
		}
	}	
}

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

package wyclipse.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import wyclipse.core.builder.WhileyPath;
import wyclipse.core.builder.WhileyProjectBuilder;

/**
 * <p>Represents the fundamental building block of the Wyclipse plugin. Attaching a
 * Whiley Nature to a project gives it the ability to compile and execute Whiley
 * files. The Whiley nature reads its build configuration from the
 * ".whileypath", which operates in a similar fashion as for JDT's ".classpath".</p>
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyNature implements IProjectNature {

	private IProject project;

	private WhileyProjectBuilder whileyProjectBuilder;
	
	@Override
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		String[] natures = desc.getNatureIds();
			
		String[] newNatures = new String[natures.length+1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = Activator.WYCLIPSE_NATURE_ID;
		desc.setNatureIds(newNatures);
		project.setDescription(desc, null);		
	}

	@Override
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IProject getProject() {
		return this.project;
	}
	
	/**
	 * Load the whileypath associated with this project. This is loaded from the
	 * <code>.whileypath</code> configuration file.
	 * 
	 * @return
	 */
	public WhileyPath getWhileyPath() throws CoreException {
		try {
			IFile file = project.getFile(".whileypath");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file.getContents());

			doc.getDocumentElement().normalize();
			return WhileyPath.fromXmlDocument(doc);
		} catch (ParserConfigurationException e) {
			return getDefaultWhileyPath();
		} catch (SAXException e) {
			return getDefaultWhileyPath();
		} catch (IOException e) {
			return getDefaultWhileyPath();
		} 
	}

	/**
	 * Write the whileypath associated with this project. This is stored in the
	 * file ".whileypath".
	 * 
	 * @param whileyPath
	 * @param monitor
	 * @throws CoreException
	 */
	public void setWhileyPath(WhileyPath whileyPath, IProgressMonitor monitor)
			throws CoreException {
		setWhileyPath(project,whileyPath,monitor);		
	}
	
	/**
	 * Set the project that this nature is associated with. This method gets
	 * called when the nature is first created, and therefore it needs to load
	 * the build configuration from the ".whileypath" file.
	 */
	@Override
	public void setProject(IProject project) {
		this.project = project;		
	}	
	
	public void setWhileyProjectBuilder(WhileyProjectBuilder builder) {
		this.whileyProjectBuilder = builder;
	}
	
	/**
	 * Return the mapping from standard library names to their absolute path
	 * location. Standard libraries are effectively built in libraries (e.g.
	 * wyrt) which have standard names. This means they can be specified on the
	 * whileypath without giving an absolute location.
	 * 
	 * @return
	 */
	public Map<String,IPath> getStandardLibraries() {
		HashMap<String,IPath> stdlibs = new HashMap<String,IPath>();
		stdlibs.put("Default WyRT", Activator.WHILEY_RUNTIME_JAR_IPATH);
		return stdlibs;
	}
	
	// ===============================================================================================
	// Helpers
	// ===============================================================================================
	
	/**
	 * Initialise the whileypath for a given project. Specifically, this will
	 * check whether or not an existing whileypath file exists; if not, a
	 * default will be used.
	 * 
	 * @param project
	 * @param monitor
	 */
	public static void initialiseWhileyPath(IProject project,
			IProgressMonitor monitor) throws CoreException {
		setWhileyPath(project, getDefaultWhileyPath(), monitor);
	}
	
	/**
	 * Construct a default whileypath to be used in the case when no whileypath
	 * exists already, and we can't find anything which helps us to guess a
	 * whileypath.
	 * 
	 * @return
	 */
	public static WhileyPath getDefaultWhileyPath() {
		Path sourceFolder = new Path("src");
		Path defaultOutputFolder = new Path("bin");

		WhileyPath.BuildRule defaultAction = new WhileyPath.BuildRule(
				sourceFolder, "**/*.whiley");

		WhileyPath whileypath = new WhileyPath(defaultOutputFolder, defaultAction);
		whileypath.setEnableRuntimeAssertions(true);
		return whileypath;
	}	


	/**
	 * Write a given whileypath into the ".whileypath" file of a given project.
	 * 
	 * @param whileyPath
	 * @param monitor
	 * @throws CoreException
	 */
	public static void setWhileyPath(IProject project, WhileyPath whileyPath, IProgressMonitor monitor)
			throws CoreException {
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

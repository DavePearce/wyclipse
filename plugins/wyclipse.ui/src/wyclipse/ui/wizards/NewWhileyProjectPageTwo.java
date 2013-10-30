package wyclipse.ui.wizards;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import wyclipse.core.WhileyNature;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.pages.WhileyPathConfigurationControl;

public class NewWhileyProjectPageTwo extends WizardPage {
	
	/**
	 * Stores the current known location of the project, or null if this is
	 * unknown. This is useful for determining when the location hasn't changed
	 * since the last time this page was visited. This is because it means we
	 * can avoid throwing away data about the whileypath which the user may have
	 * configured.
	 */
	protected URI location;
	
	/**
	 * The control which manages the whileypath configuration. This allows the
	 * user to add/remove build rules, configure targets, etc.
	 */
	protected WhileyPathConfigurationControl wpControl;
	
	protected NewWhileyProjectPageTwo() {
		super("Whiley Project Settings");
		setTitle("Whiley Project Settings");
		setDescription("Configure the Whiley Build Path");
	}

	public WhileyPath getWhileyPath() {
		return wpControl.getWhileyPath();
	}
	
	@Override
	public void createControl(Composite parent) {;
		wpControl = new WhileyPathConfigurationControl(getShell());
		Composite composite = wpControl.create(parent);
		setControl(composite);
	}
	
	@Override
	public void setPreviousPage(IWizardPage page) {
		super.setPreviousPage(page);
		
		// This is the signal that the previous page is finished, and that we're
		// now visible.  At this point, we want to run the whileypath detection.
		
		URI location = ((NewWhileyProjectPageOne)page).getLocationURI();
		
		System.out.println("PROJECT LOCATION: " + location.getPath());
		
		if(!location.equals(this.location)) { 
			// So, the location has changed since the last time we were here.
			// Therefore, redetect the WhileyPath based on the new location.
			// Observe that this will destroy any previous information the user
			// has configured for the WhileyPath.			
			wpControl.setWhileyPath(detectWhileyPath(location, (NewWhileyProjectPageOne) page));
			this.location = location;
			System.out.println("GOT HERE");
		}
	}
	
	// ======================================================================
	// WhileyPath Helpers
	// ======================================================================

	/**
	 * Determine an appropriate initial whileypath. This is done by first
	 * checking whether there is already a whileypath; if not, we attempt to
	 * detect the appropriate whileypath; finally, we fall back to a default;
	 * 
	 * @return
	 */
	protected WhileyPath detectWhileyPath(URI location, NewWhileyProjectPageOne previousPage) {
		WhileyPath whileypath;
		
		// First, determine whether or not a ".whileypath" file already exists.
		// If it does, then we simply load that and return it. Observe that we
		// have to use absolute addressing via java.io.File since the project
		// folder may not be located in a position relative to the workspace.
		File folder = new File(location);
				
		if (folder.exists() && folder.isDirectory()) {
			// Yes, project location already exists. Therefore, there's a chance
			// that a ".whileypath" file might already exist.
			File file = new File(folder,".whileypath");
			if(file.exists()) {
				// Yes, there is an existing whiley path. Therefore, load and
				// return it.
				whileypath = loadWhileyPathFromExistingFile(file);
				if(whileypath != null) {
					return whileypath;
				}
			}
		}
		
		// Second, attempt to auto-configure the whiley path from existing
		// resources.
		
		// FIXME: attempt to auto-configure whiley path
		
		// Third, return the default whiley path, augmented with the chosen
		// Runtime Environment.
		WhileyPath wp = WhileyNature.getDefaultWhileyPath();
		wp.getEntries().add(
				new WhileyPath.StandardLibrary(previousPage.getWhileyRuntime()));

		return wp;
	}
	
	protected WhileyPath loadWhileyPathFromExistingFile(File file) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file.getPath());
			doc.getDocumentElement().normalize();
			return WhileyPath.fromXmlDocument(doc);
		} catch (ParserConfigurationException e) {
			return null; // whileypath corrupted?
		} catch (SAXException e) {
			return null; // whileypath corrupted?
		} catch (IOException e) {
			return null; // whileypath corrupted?
		} 
	}
}

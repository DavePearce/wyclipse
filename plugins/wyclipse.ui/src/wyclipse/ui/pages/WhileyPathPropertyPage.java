package wyclipse.ui.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import wyclipse.core.Activator;
import wyclipse.core.WhileyNature;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.util.VirtualContainer;

/**
 * This is the property page entitled "Whiley Build Path" which shows on all
 * Whiley Projects under the "Properties" menu. The purpose of this page is to
 * allow the user to modify the Whiley Build Path settings (which are stored in
 * the <code>.whileypath</code> file). For example, they might wish to enable /
 * disable verification on a source folder; or add a library to be linked
 * against, etc.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyPathPropertyPage extends PropertyPage {
	
	private WhileyPath whileypath;
	
	private WhileyPathConfigurationControl wpControl;
	
	public WhileyPathPropertyPage() throws CoreException {
		super();
		setDescription("Configure the Whiley Build Path");
	}

	@Override
	protected Control createContents(Composite parent) {
		IProject project = (IProject) getElement();
		this.whileypath = getWhileyPath();
		this.wpControl = new WhileyPathConfigurationControl(getShell(),
				new VirtualContainer(project.getLocation()), whileypath);
		Composite composite = wpControl.create(parent);
		composite.pack();
		return composite;
	}
	
	public boolean performOk() {
		try {
			getWhileyNature().setWhileyPath(whileypath, null);
			wpControl.instantiateWhileyPath((IProject) getElement(), null);
		} catch (CoreException e) {
			MessageDialog.openError(getShell(),
					"There was an error configuring the Whiley Path",
					e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public WhileyPath getWhileyPath() {
		try {
			return getWhileyNature().getWhileyPath();
		} catch (CoreException e) {
			// Fail-safe backup --- get the default path.
			return WhileyNature.getDefaultWhileyPath();
		}
	}
	
	public WhileyNature getWhileyNature() throws CoreException {
		IProject project = (IProject) getElement();
		return (WhileyNature) project.getNature(Activator.WYCLIPSE_NATURE_ID);
	}
}

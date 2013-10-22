package wyclipse.ui.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import wyclipse.core.Activator;
import wyclipse.core.WhileyNature;
import wyclipse.core.builder.WhileyPath;

public class WhileyPathPropertyPage extends PropertyPage {
	
	private WhileyPath whileypath;
	
	public WhileyPathPropertyPage() throws CoreException {
		super();
		setDescription("Properties for the Whiley Compiler");
	}

	@Override
	protected Control createContents(Composite parent) {
		this.whileypath = getWhileyPath();
		WhileyPathConfigurationControl wpControl = new WhileyPathConfigurationControl(
				getShell(), (IProject) getElement(), whileypath);
		Composite composite = wpControl.create(parent);
	
		composite.pack();
		return composite;
	}
	
	public boolean performOk() {
		try {
			getWhileyNature().setWhileyPath(whileypath, null);
		} catch (CoreException e) {
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

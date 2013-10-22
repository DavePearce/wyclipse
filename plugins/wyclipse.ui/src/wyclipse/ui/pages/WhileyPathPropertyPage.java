package wyclipse.ui.pages;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.dialogs.PropertyPage;

import wybs.util.Trie;
import wyc.lang.WhileyFile;
import wyclipse.core.Activator;
import wyclipse.core.WhileyNature;
import wyclipse.core.builder.WhileyPath;
import wyil.lang.WyilFile;

import wyclipse.core.WhileyNature;

public class WhileyPathPropertyPage extends PropertyPage {
	
	private WhileyPath whileypath;
	
	private final String VERIFICATION_TITLE = "Enable Verification";
	
	private Button verificationEnable;
	
	public WhileyPathPropertyPage() throws CoreException {
		super();
		setDescription("Properties for the Whiley Compiler");
	}

	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		//Label for path field
		Label label = new Label(composite, SWT.NONE);
		label.setText(VERIFICATION_TITLE);

		// Verification enable check bos
		verificationEnable = new Button(composite, SWT.CHECK);	
		
		try {
			String ve =
					((IResource) getElement()).getPersistentProperty(
							WhileyNature.VERIFICATION_PROPERTY);
			if(ve != null) {
				verificationEnable.setSelection(ve.equals("true"));
			} else {
				verificationEnable.setSelection(WhileyNature.VERIFICATION_DEFAULT);
			}
		} catch (CoreException e) {
			verificationEnable.setSelection(WhileyNature.VERIFICATION_DEFAULT);
		}
	}
	
	private void addSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	private void addSecondSection(Composite parent) {
		
	}
	
	@Override
	protected Control createContents(Composite parent) {
		this.whileypath = getWhileyPath();
		WhileyPathConfigurationControl wpControl = new WhileyPathConfigurationControl(whileypath);
		Composite composite = wpControl.create(parent);
		
//		Composite composite = new Composite(parent, SWT.NONE);
//		GridLayout layout = new GridLayout();
//		composite.setLayout(layout);
//		GridData data = new GridData(GridData.FILL);
//		data.grabExcessHorizontalSpace = true;
//		composite.setLayoutData(data);
//

		
//		addFirstSection(composite);
//		addSeparator(composite);
//		addSecondSection(composite);
		
		composite.pack();
		return composite;
	}

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
				
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}
	
	protected void performDefaults() {
		super.performDefaults();
		verificationEnable.setSelection(true);
	}
	
	public boolean performOk() {
		// Store properties persistently.
		try {			
			getWhileyNature().setVerificationEnable(verificationEnable.getSelection());
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

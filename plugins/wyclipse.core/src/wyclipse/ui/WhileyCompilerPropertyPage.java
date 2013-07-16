package wyclipse.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.dialogs.PropertyPage;

public class WhileyCompilerPropertyPage extends PropertyPage {
	
	private final String VERIFICATION_PROPERTY = "VERIFICATION";
	private final String VERIFICATION_TITLE = "Enable Verification";
	private final boolean VERIFICATION_DEFAULT = true;
	
	private Button verificationEnable;
	
	public WhileyCompilerPropertyPage() {
		super();
		//setPreferenceStore(RmpPlugin.getDefault().getPreferenceStore());
		setDescription("Properties for the Whiley Compiler");				
	}

	private void addFirstSection(Composite parent) {
		Composite composite = createDefaultComposite(parent);

		//Label for path field
		Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText(VERIFICATION_TITLE);

		// Path text field
		verificationEnable = new Button(composite, SWT.CHECK);		
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
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
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
		// store the value in the owner text field
		try {
			((IResource) getElement()).setPersistentProperty(new QualifiedName(
					"", VERIFICATION_PROPERTY), Boolean
					.toString(verificationEnable.getSelection()));
		} catch (CoreException e) {
			return false;
		}
		return true;
	}
}

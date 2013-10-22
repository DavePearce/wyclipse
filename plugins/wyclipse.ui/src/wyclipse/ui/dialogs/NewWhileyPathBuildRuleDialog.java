package wyclipse.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class NewWhileyPathBuildRuleDialog extends Dialog {
	
	public NewWhileyPathBuildRuleDialog(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		// =====================================================================
		// Configure Grid
		// =====================================================================

		GridLayout layout = new GridLayout();		
		layout.numColumns = 3;
		layout.verticalSpacing = 9;	
		layout.marginWidth = 20;
		container.setLayout(layout);

		// =====================================================================
		// Done
		// =====================================================================
		createLabel(container, "Source Folder:", 1);		
		createText(container, "", 2);
		createButton(container, "Browse...");

		return container;
	}
}

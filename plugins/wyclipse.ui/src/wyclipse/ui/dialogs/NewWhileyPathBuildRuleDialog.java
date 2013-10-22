package wyclipse.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import wyclipse.ui.util.WyclipseUI;

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
		WyclipseUI.createLabel(container, "Source Folder:", 1);		
		WyclipseUI.createText(container, "", 1, 200);
		WyclipseUI.createButton(container, "Browse...", 120);

		return container;
	}
}

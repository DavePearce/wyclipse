package wyclipse.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import wyclipse.ui.util.WyclipseUI;

public class NewFolderDialog extends Dialog {

	private Text folderText;
	private String result;
	
	protected NewFolderDialog(Shell parentShell) {
		super(parentShell);				
	}
	
	public String getResult() {
		return result;
	}
	
	@Override
	public Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		// =====================================================================
		// Configure Grid
		// =====================================================================

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.marginWidth = 20;

		container.setLayout(layout);

		// =====================================================================
		// Label and Text
		// =====================================================================
		WyclipseUI.createLabel(container, "Folder Name", 1);
		this.folderText = WyclipseUI.createText(container, "", 1, 200);

		// =====================================================================
		// Label and Text
		// =====================================================================

		return container;
	}
	
	@Override
	public void okPressed() {
		result = folderText.getText();
		super.okPressed();
	}
}

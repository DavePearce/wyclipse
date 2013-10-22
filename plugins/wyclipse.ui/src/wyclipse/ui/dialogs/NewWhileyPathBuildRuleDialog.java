package wyclipse.ui.dialogs;

import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import wyclipse.ui.util.WyclipseUI;

/**
 * <p>
 * Responsible for providing a simple and clean interface to creating a
 * "build rule". Roughly speaking, a build rule compiles files from one content
 * type (by default, Whiley) to a given content type (e.g. WyIL, JVM Class
 * Files, etc). Source files are located in one folder, and an output folder is
 * given for the binary files.
 * </p>
 * <p>
 * A build rule also allows the builder to be configured. For example, whether
 * or not verification is enabled, whether or not intermediate files (e.g. WyAL,
 * WyIL, etc) are produced.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
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
		
		WyclipseUI.createLabel(container, "Includes:", 1);		
		WyclipseUI.createText(container, "", 2);
		
		WyclipseUI.createLabel(container, "Target:", 1);
		WyclipseUI.createText(container, "", 2); // to be removed
		
		// create check box
		WyclipseUI.createCheckBox(container, "Use Default Output Folder",3);
		WyclipseUI.createLabel(container, "Output Folder:", 1);		
		WyclipseUI.createText(container, "", 1, 200);
		WyclipseUI.createButton(container, "Browse...", 120);
		
		WyclipseUI.createSeparator(container, 3);
		
		WyclipseUI.createCheckBox(container, "Enable Verification",3);
		WyclipseUI.createCheckBox(container, "Generate Verification Conditions (i.e. WyAL files)", 3);
		
		WyclipseUI.createSeparator(container, 3);
		
		WyclipseUI.createCheckBox(container, "Enable Advanced Configuration",3);
		WyclipseUI.createCheckBox(container, "Generate Intermediate Files (i.e. WyIL files)", 3);
				
		return container;
	}
}

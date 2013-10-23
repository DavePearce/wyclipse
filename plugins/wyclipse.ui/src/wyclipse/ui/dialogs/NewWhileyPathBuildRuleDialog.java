package wyclipse.ui.dialogs;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import wybs.util.Trie;
import wyclipse.core.builder.WhileyPath;
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
	// Data item being constructed
	private WhileyPath.BuildRule buildRule;
	
	// Source / Target Group
	private Text sourceFolderText;	
	private Text sourceIncludesText;
	private Text targetText;
	
	// Output Folder Group
	private Button useDefaultOutputFolder;
	private Text outputFolderText;	
	private Button outputFolderBrowseButton;

	// Verification Group
	private Button enableVerification;
	private Button generateVerificationConditions;

	// Advanced Config Group
	private Button enableAdvancedConfiguration;
	private Button generateWyilFiles;

	public NewWhileyPathBuildRuleDialog(Shell parentShell,
			WhileyPath.BuildRule buildRule) {
		super(parentShell);
		this.buildRule = buildRule;
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
		// Configure Source / Target Group
		// =====================================================================
		
		WyclipseUI.createLabel(container, "Source Folder:", 1);		
		sourceFolderText = WyclipseUI.createText(container, "", 1, 200);
		WyclipseUI.createButton(container, "Browse...", 120);
		
		WyclipseUI.createLabel(container, "Includes:", 1);		
		sourceIncludesText = WyclipseUI.createText(container, "", 2);
		
		WyclipseUI.createLabel(container, "Target:", 1);
		targetText = WyclipseUI.createText(container, "", 2); // to be removed

		// =====================================================================
		// Configure Output Folder Group
		// =====================================================================

		// create check box
		useDefaultOutputFolder = WyclipseUI.createCheckBox(container, "Use Default Output Folder",3);
		WyclipseUI.createLabel(container, "Output Folder:", 1);		
		outputFolderText = WyclipseUI.createText(container, "", 1, 200);
		outputFolderBrowseButton = WyclipseUI.createButton(container, "Browse...", 120);
		
		WyclipseUI.createSeparator(container, 3);

		// =====================================================================
		// Configure Verification Group
		// =====================================================================
		
		enableVerification = WyclipseUI.createCheckBox(container,
				"Enable Verification", 3);
		generateVerificationConditions = WyclipseUI.createCheckBox(container,
				"Generate Verification Conditions (i.e. WyAL files)", 3);

		WyclipseUI.createSeparator(container, 3);
		
		// =====================================================================
		// Configure Advanced Configuration Group
		// =====================================================================
		
		enableAdvancedConfiguration = WyclipseUI.createCheckBox(container,
				"Enable Advanced Configuration", 3);
		generateWyilFiles = WyclipseUI.createCheckBox(container,
				"Generate Intermediate Files (i.e. WyIL files)", 3);

		// =====================================================================
		// Initialise Data
	    // =====================================================================
		writeSourceTargetGroup();
		writeOutputFolderGroup();
		writeVerificationGroup();
		initialiseAdvancedConfigurationGroup();
		
		// =====================================================================
		// Done
	    // =====================================================================
						
		return container;
	}
	
	@Override
	public void okPressed() {
		// FIXME: need more!!
		readSourceTargetGroup();
		super.okPressed();
	}
	
	// =========================================================================
	// Write data to fields from WhileyPath
	// =========================================================================
	
	private void writeSourceTargetGroup() {		
		sourceFolderText.setText(buildRule.getSourceFolder().toString());
		sourceIncludesText.setText(buildRule.getSourceIncludes().toString());
		//targetText.setText(buildRule.getTarget().toString());
	}
	
	private void writeOutputFolderGroup() {
		if(buildRule.getOutputFolder() != null) {
			useDefaultOutputFolder.setSelection(false);
			outputFolderText.setText(buildRule.getOutputFolder().toString());
			outputFolderText.setEnabled(true);
			outputFolderBrowseButton.setEnabled(true);
		} else {
			useDefaultOutputFolder.setSelection(true);
			outputFolderText.setEnabled(false);
			outputFolderBrowseButton.setEnabled(false);
		}
	}
	
	private void writeVerificationGroup() {
		// Fow now, not supported!
		enableVerification.setSelection(false);
		generateVerificationConditions.setEnabled(false);
	}
	
	private void initialiseAdvancedConfigurationGroup() {
		// Fow now, not supported!
		enableAdvancedConfiguration.setSelection(false);
		generateWyilFiles.setEnabled(false);
	}
	
	// =========================================================================
	// Read data from fields into WhileyPath
	// =========================================================================

	private void readSourceTargetGroup() {		
		buildRule.setSourceFolder(new Path(sourceFolderText.getText()));
		buildRule.setSourceIncludes(Trie.fromString(sourceIncludesText.getText()));
		if(useDefaultOutputFolder.getSelection()) {
			buildRule.setOutputFolder(null);
		} else {
			buildRule.setOutputFolder(new Path(outputFolderText.getText()));
		}
	}
}

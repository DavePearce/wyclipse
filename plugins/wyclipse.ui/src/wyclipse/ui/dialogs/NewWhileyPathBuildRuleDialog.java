package wyclipse.ui.dialogs;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private Combo targetCombo;
	
	// Output Folder Group
	private Button useDefaultOutputFolder;
	private Label outputFolderLabel;	
	private Text outputFolderText;	
	private Button outputFolderBrowseButton;
	private Button enableVerification;
	private Button enableRuntimeAssertions;
	
	// Advanced Config Group
	private Button generateWyIL;
	private Button generateWyAL;

	public NewWhileyPathBuildRuleDialog(Shell shell,
			WhileyPath.BuildRule buildRule) {
		super(shell);
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
		
		WyclipseUI.createLabel(container, "Target Platform:", 1);
		targetCombo = WyclipseUI.createCombo(container, 2,
				"Java Virtual Machine (Default)"); 

		// =====================================================================
		// Configure Output Folder Group
		// =====================================================================

		// create check box
		useDefaultOutputFolder = WyclipseUI.createCheckBox(container, "Use Default Output Folder",3);
		outputFolderLabel = WyclipseUI.createLabel(container, "Output Folder:", 1);		
		outputFolderText = WyclipseUI.createText(container, "", 1, 200);
		outputFolderBrowseButton = WyclipseUI.createButton(container, "Browse...", 120);
		
		useDefaultOutputFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleUseDefaultOutputFolder();
			}
		});	
		
		// =====================================================================
		// Configure Verification Group
		// =====================================================================
		
		enableVerification = WyclipseUI.createCheckBox(container,
				"Enable Verification", 3);
		enableRuntimeAssertions = WyclipseUI.createCheckBox(container,
				"Enable RuntimeAssertions", 3);
		
		WyclipseUI.createSeparator(container, 3);
		
		// =====================================================================
		// Configure Advanced Configuration Group
		// =====================================================================
				
		WyclipseUI.createLabel(container,"Advance Configuration Options",3);
		generateWyIL = WyclipseUI.createCheckBox(container,
				"Generate Intermediate Files (i.e. WyIL files)", 3);
		generateWyAL = WyclipseUI.createCheckBox(container,
				"Generate Verification Conditions (i.e. WyAL files)", 3);

		// =====================================================================
		// Initialise Data
	    // =====================================================================
		writeSourceTargetGroup();
		writeOutputFolderGroup();
		writeVerificationGroup();
		writeAdvancedConfigurationGroup();
		
		// =====================================================================
		// Done
	    // =====================================================================
						
		return container;
	}

	// =========================================================================
	// Event Handlers
	// =========================================================================
	
	@Override
	public void okPressed() {
		readSourceTargetGroup();
		readVerificationGroup();
		readAdvancedGroup();
		super.okPressed();
	}
	
	private void handleUseDefaultOutputFolder() {
		// useDefaultOutputFolder control toggled.
		if (useDefaultOutputFolder.getSelection()) {
			outputFolderLabel.setEnabled(false);
			outputFolderLabel.setForeground(outputFolderLabel.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GRAY)); // force gray
			outputFolderText.setEnabled(false);
			outputFolderBrowseButton.setEnabled(false);
		} else {
			outputFolderLabel.setEnabled(true);
			outputFolderLabel.setForeground(null); // set default
			outputFolderText.setEnabled(true);
			outputFolderBrowseButton.setEnabled(true);
		}
	}
	
	// =========================================================================
	// Write data to fields from WhileyPath
	// =========================================================================
	
	private void writeSourceTargetGroup() {		
		sourceFolderText.setText(buildRule.getSourceFolder().toString());
		sourceIncludesText.setText(buildRule.getSourceIncludes().toString());
		targetCombo.setText("Whiley Virtual Machine");
	}
	
	private void writeOutputFolderGroup() {
		if(buildRule.getOutputFolder() != null) {
			useDefaultOutputFolder.setSelection(false);
			outputFolderLabel.setEnabled(true);
			outputFolderLabel.setForeground(null); // set default
			outputFolderText.setText(buildRule.getOutputFolder().toString());
			outputFolderText.setEnabled(true);
			outputFolderBrowseButton.setEnabled(true);
		} else {
			useDefaultOutputFolder.setSelection(true);
			outputFolderLabel.setEnabled(false);
			outputFolderLabel.setForeground(outputFolderLabel.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GRAY)); // force gray
			outputFolderText.setEnabled(false);
			outputFolderBrowseButton.setEnabled(false);
		}
	}
	
	private void writeVerificationGroup() {
		enableVerification.setSelection(buildRule.getEnableVerification());
		enableRuntimeAssertions.setSelection(buildRule.getEnableRuntimeAssertions());
	}
	
	private void writeAdvancedConfigurationGroup() {
		// Fow now, not supported!
		generateWyIL.setSelection(buildRule.getGenerateWyIL());
		generateWyAL.setSelection(buildRule.getGenerateWyAL());
	}
	
	// =========================================================================
	// Read data from fields into WhileyPath
	// =========================================================================

	private void readSourceTargetGroup() {		
		buildRule.setSourceFolder(new Path(sourceFolderText.getText()));
		buildRule.setSourceIncludes(sourceIncludesText.getText());
		if(useDefaultOutputFolder.getSelection()) {
			buildRule.setOutputFolder(null);
		} else {
			buildRule.setOutputFolder(new Path(outputFolderText.getText()));
		}
	}
	
	private void readVerificationGroup() {
		buildRule.setEnableVerification(enableVerification.getSelection());
		buildRule.setEnableRuntimeAssertions(enableRuntimeAssertions.getSelection());
	}
	
	private void readAdvancedGroup() {
		buildRule.setGenerateWyIL(generateWyIL.getSelection());
		buildRule.setGenerateWyAL(generateWyAL.getSelection());
	}
}

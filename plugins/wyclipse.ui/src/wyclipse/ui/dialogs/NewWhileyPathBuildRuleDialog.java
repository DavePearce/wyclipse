package wyclipse.ui.dialogs;

import java.io.File;
import java.net.URI;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import wybs.util.Trie;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.util.VirtualContainer;
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
	/**
	 * The build rule is the data item that we're configuring in this dialog.
	 */
	private WhileyPath.BuildRule buildRule;
	
	/**
	 * Provides a "virtual" project within which we can see existing folders
	 * within the project, and add new folders. However, new folders are not
	 * actually created on the filesystem and remain "virtual".
	 */
	private VirtualContainer projectLocation;
	
	// Source / Target Group
	private Text sourceFolderText;	
	private Text sourceIncludesText;
	private Combo targetCombo;
	
	// Output Folder Group
	private Button useFolderSpecificSettings;
	private Label outputFolderLabel;	
	private Text outputFolderText;	
	private Button outputFolderBrowseButton;
	private Button enableVerification;
	private Button enableRuntimeAssertions;
	
	// Advanced Config Group
	private Button generateWyIL;
	private Button generateWyAL;

	public NewWhileyPathBuildRuleDialog(Shell shell,
			WhileyPath.BuildRule buildRule, VirtualContainer project) {
		super(shell);
		this.buildRule = buildRule;
		this.projectLocation = project;
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
		Button sourceFolderBrowseButton = WyclipseUI.createButton(container, "Browse...",
				120);
		
		WyclipseUI.createLabel(container, "Includes:", 1);		
		sourceIncludesText = WyclipseUI.createText(container, "", 2);
		
		WyclipseUI.createLabel(container, "Target Platform:", 1);
		targetCombo = WyclipseUI.createCombo(container, 2,
				"Java Virtual Machine (Default)"); 

		sourceFolderBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseSourceFolder();
			}
		});

		// =====================================================================
		// Configure Output Folder Group
		// =====================================================================

		// create check box
		useFolderSpecificSettings = WyclipseUI.createCheckBox(container, "Enable Folder Specific Settings",3);
		
		// =====================================================================
		// Configure Folder Specific Settings
		// =====================================================================
		
		Group settings = WyclipseUI.createGroup(container,"Folder Specific Settings",SWT.SHADOW_ETCHED_IN, 3, 3);
		
		enableVerification = WyclipseUI.createCheckBox(settings,
				"Enable Verification", 3);
		enableRuntimeAssertions = WyclipseUI.createCheckBox(settings,
				"Enable RuntimeAssertions", 3);
				
		outputFolderLabel = WyclipseUI.createLabel(settings, "Output Folder:", 1);		
		outputFolderText = WyclipseUI.createText(settings, "", 1, 200);
		outputFolderBrowseButton = WyclipseUI.createButton(settings, "Browse...", 120);
		
		outputFolderBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseOutputFolder();
			}
		});
		
		useFolderSpecificSettings.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleEnableFolderSpecificSettings();
			}
		});	
						
		generateWyIL = WyclipseUI.createCheckBox(settings,
				"Generate Intermediate Files (i.e. WyIL files)", 3);
		generateWyAL = WyclipseUI.createCheckBox(settings,
				"Generate Verification Conditions (i.e. WyAL files)", 3);

		// =====================================================================
		// Initialise Data
	    // =====================================================================
		write();
		
		// =====================================================================
		// Done
	    // =====================================================================
		
		container.pack();
		
		return container;
	}

	// =========================================================================
	// Event Handlers
	// =========================================================================
	
	@Override
	public void okPressed() {
		read();
		super.okPressed();
	}
	
	private void handleEnableFolderSpecificSettings() {
		// useDefaultOutputFolder control toggled.
		if (useFolderSpecificSettings.getSelection()) {
			outputFolderLabel.setEnabled(true);
			outputFolderLabel.setForeground(null); // set default
			outputFolderText.setEnabled(true);
			outputFolderBrowseButton.setEnabled(true);
			
			enableVerification.setEnabled(true);
			enableRuntimeAssertions.setEnabled(true);
			generateWyIL.setEnabled(true);
			generateWyAL.setEnabled(true);
		} else {
			outputFolderLabel.setEnabled(false);
			outputFolderLabel.setForeground(outputFolderLabel.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GRAY)); // force gray
			outputFolderText.setEnabled(false);
			outputFolderBrowseButton.setEnabled(false);
			
			enableVerification.setEnabled(false);
			enableRuntimeAssertions.setEnabled(false);
			generateWyIL.setEnabled(false);
			generateWyAL.setEnabled(false);
		}
	}
	
	private void handleBrowseSourceFolder() {
		VirtualContainerSelectionDialog dialog = new VirtualContainerSelectionDialog(getShell(),
				projectLocation);
		if (dialog.open() == Window.OK) {
			IPath path = dialog.getResult();
			sourceFolderText.setText(path.toString());
		}
	}
	
	private void handleBrowseOutputFolder() {
		VirtualContainerSelectionDialog dialog = new VirtualContainerSelectionDialog(getShell(),
				projectLocation);
		if (dialog.open() == Window.OK) {
			IPath path = dialog.getResult();
			outputFolderText.setText(path.toString());
		}
	}
	
	// =========================================================================
	// Write data to fields from WhileyPath
	// =========================================================================
	
	private void write() {		
		sourceFolderText.setText(buildRule.getSourceFolder().toString());
		sourceIncludesText.setText(buildRule.getSourceIncludes().toString());
		targetCombo.setText("Whiley Virtual Machine");
	
		if(buildRule.getEnableLocalSettings()) {
			useFolderSpecificSettings.setSelection(true);
			outputFolderLabel.setEnabled(true);
			outputFolderLabel.setForeground(null); // set default
			outputFolderText.setText(buildRule.getOutputFolder().toString());
			outputFolderText.setEnabled(true);
			outputFolderBrowseButton.setEnabled(true);
			
		} else {
			useFolderSpecificSettings.setSelection(false);
			outputFolderLabel.setEnabled(false);
			outputFolderLabel.setForeground(outputFolderLabel.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GRAY)); // force gray
			outputFolderText.setEnabled(false);
			outputFolderBrowseButton.setEnabled(false);
		}
		
		enableVerification.setSelection(buildRule.getEnableVerification());
		enableRuntimeAssertions.setSelection(buildRule.getEnableRuntimeAssertions());
		generateWyIL.setSelection(buildRule.getGenerateWyIL());
		generateWyAL.setSelection(buildRule.getGenerateWyAL());
	}
	
	// =========================================================================
	// Read data from fields into WhileyPath
	// =========================================================================

	private void read() {		
		buildRule.setSourceFolder(new Path(sourceFolderText.getText()));
		buildRule.setSourceIncludes(sourceIncludesText.getText());
		buildRule.setEnableLocalSettings(useFolderSpecificSettings
				.getSelection());
		if(useFolderSpecificSettings.getSelection()) {
			buildRule.setOutputFolder(null);		
		} else {
			buildRule.setOutputFolder(new Path(outputFolderText.getText()));
			buildRule.setEnableVerification(enableVerification.getSelection());
			buildRule.setEnableRuntimeAssertions(enableRuntimeAssertions.getSelection());
			buildRule.setGenerateWyIL(generateWyIL.getSelection());
			buildRule.setGenerateWyAL(generateWyAL.getSelection());
		}
	}	
}

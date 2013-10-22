package wyclipse.ui.pages;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.WizardNewFolderMainPage;
import org.eclipse.ui.internal.ide.dialogs.FileFolderSelectionDialog;

import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.dialogs.NewWhileyPathBuildRuleDialog;
import wyclipse.ui.util.WhileyPathViewer;

/**
 * Provides an abstract class which constructs the standard page for configuring
 * the whileypath. This is used in at least two places:
 * <code>NewWhileyProjectWizard</code> and <code>WhileyPathPropertyPage</code>.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyPathConfigurationControl {
	private Shell shell;
	private IProject project;
	private WhileyPath whileypath;
	
	// WhileyPath view + controls
	private WhileyPathViewer whileyPathViewer;
	
	// Default Output Folder Controls
	private Label defaultOutputFolderLabel;
	private Text defaultOutputFolderText; 
	private Button defaultOutputFolderBrowseButton;
	
	public WhileyPathConfigurationControl(Shell shell, IProject project, WhileyPath whileypath) {
		this.shell = shell;
		this.project = project;
		this.whileypath = whileypath;	
	}
	
	public Composite create(Composite parent) {				
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
		// Middle Section
		// =====================================================================		
				
		// Create viewer which is 2 columns wide and 3 rows deep.
		whileyPathViewer = createWhileyPathViewer(container, whileypath, 2, 3);						
		Button addButton = createButton(container, "Add Rule...");
		Button editButton = createButton(container, "Edit");
		Button removeButton = createButton(container, "Remove");		
		
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleAddRule();
			}
		});
		
		removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleRemoveRule();
			}
		});
		
		// =====================================================================
		// Bottom Section
		// =====================================================================
		defaultOutputFolderLabel = createLabel(container, "Default Output Folder:", 3);		
		defaultOutputFolderText = createText(container, "", 2);
		defaultOutputFolderBrowseButton = createButton(container, "Browse...");

		defaultOutputFolderBrowseButton
				.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						handleBrowseDefaultOutputFolder();
					}
				});	
		
		IPath defaultOutputFolder = whileypath.getDefaultOutputFolder();
		if(defaultOutputFolder == null) {
			// No default output folder
			defaultOutputFolderLabel.setEnabled(false);
			defaultOutputFolderText.setEnabled(false);
			defaultOutputFolderBrowseButton.setEnabled(false);
		} else {
			defaultOutputFolderText.setText(defaultOutputFolder.toString());
		}
		
		container.pack();
		
		return container;
	}		
	
	// ======================================================================
	// Call Backs
	// ======================================================================

	/**
	 * This function is called when the add button is pressed.
	 */
	protected void handleAddRule() {
		NewWhileyPathBuildRuleDialog dialog = new NewWhileyPathBuildRuleDialog(shell); 		
		dialog.open();
	}
	
	/**
	 * This function is called when the remove button is pressed.
	 */
	protected void handleRemoveRule() {		
		TreeItem[] items = whileyPathViewer.getTree().getSelection();
		for (TreeItem item : items) {
			Object data = item.getData();
			if (data instanceof WhileyPath.BuildRule) {
				whileypath.getEntries().remove(data);
			}
		}
		whileyPathViewer.refresh();
	}
	
	/**
	 * This function is called when the browse button for the default output
	 * folder is called.
	 */
	protected void handleBrowseDefaultOutputFolder() {
		
		// This is a fairly simple approach to selecting a folder from the user.
		// To make it more appealing, we might like to use a simpler (custom)
		// dialog (like the JDT does). But, for now, this will do!
		
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setFilterPath(project.getLocation().toString());
		String result = dialog.open();
		if(result != null) {
			IPath path = new Path(result);
			path = path.makeRelativeTo(project.getLocation());
			defaultOutputFolderText.setText(path.toString());
		}
	}
	
	// ======================================================================
	// Helpers
	// ======================================================================
	
	protected WhileyPathViewer createWhileyPathViewer(Composite container,
			Object input, int horizontalSpan, int verticalSpan) {
		WhileyPathViewer viewer = new WhileyPathViewer(container, SWT.VIRTUAL
				| SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);		
		gd.horizontalSpan = horizontalSpan;
		gd.verticalSpan = verticalSpan;
		viewer.getTree().setLayoutData(gd);
		viewer.setInput(input);
		return viewer;
	}
	
	protected Button createButton(Composite parent, String text) {
		GridData gd = new GridData();
		gd.widthHint = 150;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setLayoutData(gd);
		return button;
	}
	
	protected Label createLabel(Composite parent, String text, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Label label = new Label(parent, SWT.NULL);
		label.setText(text);
		label.setLayoutData(gd);
		return label;
		
	}
	
	protected Text createText(Composite parent, String initialText, int horizontalSpan) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		Text text = new Text(parent, SWT.BORDER | SWT.SINGLE);
		text.setText(initialText);
		text.setLayoutData(gd);
		return text;
	}
}

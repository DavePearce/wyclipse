package wyclipse.ui.wizards;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import wybs.util.Trie;
import wyc.lang.WhileyFile;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.util.WhileyPathViewer;
import wyil.lang.WyilFile;

public class NewWhileyProjectPageTwo extends WizardPage {

	protected NewWhileyProjectPageTwo() {
		super("Whiley Project Settings");
		setTitle("Whiley Project Settings");
		setDescription("Configure the Whiley Build Path");
	}

	public WhileyPath getWhileyPath() {
		// FIXME
		return defaultWhileyPath();
	}
	
	@Override
	public void createControl(Composite parent) {				
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
		WhileyPathViewer viewer = createWhileyPathViewer(container, defaultWhileyPath(), 2, 3);						
		Button srcButton = createButton(container, "Add Folder...");
		Button editButton = createButton(container, "Edit");
		Button removeButton = createButton(container, "Remove");		
		
		// =====================================================================
		// Bottom Section
		// =====================================================================
		Label defaultOutputFolderLabel = createLabel(container, "Default Output Folder:", 3);		
		Text defaultOutputFolder = createText(container, "bin/", 2);
		Button browseButton = createButton(container, "Browse...");
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseLocation();
			}
		});		
		
		container.pack();
		
		setControl(container);
	}		
	
	protected void handleBrowseLocation() {
		
	}
	
	/**
	 * Construct a default whileypath in the case when no whileypath exists
	 * already, and we can't find anything which helps us to guess a whileypath.
	 * 
	 * @return
	 */
	protected WhileyPath defaultWhileyPath() {
		Path sourceFolder = new Path("src");
		Path defaultOutputFolder = new Path("bin");

		WhileyPath.Action defaultAction = new WhileyPath.Action(sourceFolder, Trie.fromString("**"),
				null);
			
		return new WhileyPath(defaultOutputFolder,defaultAction);
	}
	
	
	// ======================================================================
	// Helpers
	// ======================================================================
	
	protected WhileyPathViewer createWhileyPathViewer(Composite container, Object input, int horizontalSpan, int verticalSpan) {
		WhileyPathViewer viewer = new WhileyPathViewer(container, SWT.VIRTUAL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
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

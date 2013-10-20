package wyclipse.ui.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import wyclipse.core.builder.WhileyPath;
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
	private WhileyPath whileypath;
	
	
	public WhileyPathConfigurationControl(WhileyPath whileypath) {
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
		WhileyPathViewer viewer = createWhileyPathViewer(container, whileypath, 2, 3);						
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
		
		return container;
	}		
	
	protected void handleBrowseLocation() {
		
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

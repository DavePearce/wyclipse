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
import wyil.lang.WyilFile;

public class NewWhileyProjectPageTwo extends WizardPage {

	protected NewWhileyProjectPageTwo() {
		super("Whiley Project Settings");
		setTitle("Whiley Project Settings");
		setDescription("Configure the Whiley Build Path");
	}

	@Override
	public void createControl(Composite parent) {				
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();		
		layout.numColumns = 3;
		layout.verticalSpacing = 9;	
		layout.marginWidth = 20;
		container.setLayout(layout);

		
//		Button button = new Button(container, SWT.PUSH);
//		button.setText("Add Folder");		
//		
		
		// =====================================================================
		// Tree View
		// =====================================================================
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 20;
		
		final TreeViewer tree = new TreeViewer(container, SWT.VIRTUAL | SWT.BORDER);
		tree.setContentProvider(new WhileyPathContentProvider());
		tree.setLabelProvider(new WhileyPathLabelProvider());
		tree.setInput(defaultWhileyPath());		
		  
		// =====================================================================
		// Last Row
		// =====================================================================		
//		new Label(container, SWT.NULL).setText("Default Output Folder:");
//		new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
//		Text defaultOutputFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
//		gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 2;
//		defaultOutputFolder.setText("bin/");
//		defaultOutputFolder.setLayoutData(gd);
//		Button broweButton = new Button(container, SWT.PUSH);
//		broweButton.setText("Browse...");
//		broweButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				handleBrowseLocation();
//			}
//		});
//		new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
//		
		setControl(container);
	}
	
	protected void handleBrowseLocation() {
		
	}
	
	protected WhileyPath defaultWhileyPath() {
		// TODO: actually read this from the whileypath file!!

		WhileyPath whileypath = new WhileyPath();

		// The default "whileypath"
		Path src = new Path("src");
		Path bin = new Path("bin");

		// Hmmm, this is a bit complicated?

		whileypath.getEntries().add(
				new WhileyPath.SourceFolder("whiley", src, Trie
						.fromString("**"), WhileyFile.ContentType));
		whileypath.getEntries().add(
				new WhileyPath.BinaryFolder("wyil", bin, Trie.fromString("**"),
						WyilFile.ContentType));

		whileypath.getEntries().add(
				new WhileyPath.Rule("wyc", "whiley", "wyil"));

		return whileypath;
	}
	
	protected final static class WhileyPathContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof WhileyPath) {
				WhileyPath whileyPath = (WhileyPath) inputElement;
				java.util.List<WhileyPath.Entry> entries = whileyPath.getEntries();
				return entries.toArray(new Object[entries.size()]);				
			} else {
				return new Object[]{};
			}
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return new Object[]{};
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}		
	}
	
	protected final static class WhileyPathLabelProvider implements ILabelProvider {

		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getText(Object element) {
			return "Source folder";
		}
		
	}
}

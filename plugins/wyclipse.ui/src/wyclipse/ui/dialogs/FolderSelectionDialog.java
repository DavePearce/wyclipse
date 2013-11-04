package wyclipse.ui.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>
 * Responsible for allowing the user to select an existing folder relative from
 * a project root, or to add a new one. This is used in the
 * <code>NewWhileyProjectWizard</code> and the
 * <code>WhileyPathPropertyPage</code> for configuring source and output
 * folders. This dialog will display existing folders relative to the given
 * project.
 * </p>
 * 
 * <b>Note:</b> that this dialog does not actually create any new folders,
 * although it gives the appearance of doing so. This is because the folders are
 * only created when the given "transaction" is completed. That is, when the
 * used selects finish or apply on the <code>NewWhileyProjectWizard</code> or
 * <code>WhileyPathPropertyPage</code>. Thus, in the case that the user selects
 * "cancel", there is actually nothing to undo.
 * 
 * @author David J. Pearce
 * 
 */
public class FolderSelectionDialog extends Dialog {	
	private TreeNode root;
	private TreeViewer view;
	
	public FolderSelectionDialog(Shell parentShell, String rootName, File rootLocation) {
		super(parentShell);
		this.root = new TreeNode(rootName, rootLocation);
	}

	@Override
	public Control createDialogArea(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		// =====================================================================
		// Configure Grid
		// =====================================================================

		GridLayout layout = new GridLayout();		
		layout.numColumns = 1;
		layout.verticalSpacing = 9;	
		layout.marginWidth = 20;
		container.setLayout(layout);

		// =====================================================================
		// Configure TreeView
		// =====================================================================
		this.view = new TreeViewer(container, SWT.VIRTUAL | SWT.BORDER);
		this.view.setContentProvider(new ContentProvider());
		this.view.setInput(root);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		gd.widthHint = 200;
		this.view.getTree().setLayout(new GridLayout());
		this.view.getTree().setLayoutData(gd);

		// =====================================================================
		// Configure TreeView
		// =====================================================================

		container.pack();
		return container;
	}
	
	private static class TreeNode {
		private String name;
		private File root;
		private ArrayList<TreeNode> children;

		public TreeNode(String name, File root) {
			this.root = root;
			this.name = name;
			// initially children is null; only when children is requested do we
			// actually look what's there (i.e. lazily).
		}

		List<TreeNode> getChildren() {
			if (children == null) {
				children = new ArrayList<TreeNode>();
				if (root != null) {
					// non-virtual node
					File[] contents = root.listFiles();
					for (File f : contents) {
						if (f.isDirectory()) {
							children.add(new TreeNode(f.getName(), f));
						}
					}
				}
			}
			return children;
		}
		
		public String toString() {
			return name;
		}
	}
	
	/**
	 * The content provider is responsible for deconstructing the object being
	 * viewed in the viewer, so that the <code>TreeViewer</code> can navigate
	 * them. In this case, this means it deconstructs those TreeNodes.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	private final static class ContentProvider implements ITreeContentProvider {

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
			if (inputElement instanceof TreeNode) {
				TreeNode node = (TreeNode) inputElement;
				// NOTE: cannot just reuse node here.
				return new Object[] { new TreeNode(node.name, node.root) };
			} else {
				return new Object[]{};
			}
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof TreeNode) {
				TreeNode node = (TreeNode) parentElement;
				return node.getChildren().toArray();				
			} else {
				return new Object[] {};
			}
		}

		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if(element instanceof TreeNode) {
				TreeNode node = (TreeNode) element;
				return node.getChildren().size() > 0;
			}
			return false;
		}		
	}
}

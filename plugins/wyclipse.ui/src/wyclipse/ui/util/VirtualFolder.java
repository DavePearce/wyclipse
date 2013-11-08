package wyclipse.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * <p>
 * Represents a "virtual" container. This is a container which may not really
 * exist, but can be manipulated (e.g. by adding files, etc). This is useful for
 * implementing "transaction-semantics" in, for example, wizards.
 * </p>
 * <p>
 * As a concrete example, consider the <code>NewWhileyProjectWizard</code>. This
 * allows the user to identify the name and location of a new project and
 * (amongst other things) configure the Whiley build path. When configuring the
 * Whiley build path, we might want to create new source folders or output
 * folders. Such things are only actually created once the "Finished" button on
 * the wizard is pressed (i.e. end-of-transaction). Only at this point do we
 * want to create the physical folders and files configured for the Whiley build
 * path. Therefore, to implement this, we use a virtual container. This is
 * initially populated with the contents of project folder (if it exists, which
 * it may not). The user can then select items from this virtual container
 * and/or create new items.
 * </p>
 * <p>
 * The fundamental unit of abstraction is the <code>Resource</code>. This
 * extends <code>TreeNode</code> to simplify viewing a virtual file system
 * within a container.
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class VirtualFolder {	
	private IPath root;
	private ArrayList<VirtualFolder> children;

	public VirtualFolder(IPath root) {
		this.root = root;			
		// initially children is null; only when children is requested do we
		// actually look what's there (i.e. lazily).
	}

	private VirtualFolder(File root) {
		this.root = new Path(root.toString());		
		// initially children is null; only when children is requested do we
		// actually look what's there (i.e. lazily).
	}

	public List<VirtualFolder> getChildren() {
		if (children == null) {
			children = new ArrayList<VirtualFolder>();
			if (getRoot() != null) {
				// non-virtual node
				File dir = getRoot().toFile();
				if (dir.exists()) {
					File[] contents = dir.listFiles();
					for (File f : contents) {
						if (f.isDirectory()) {
							children.add(new VirtualFolder(f));
						}
					}
				}
			}
		}
		return children;
	}

	public String toString() {
		return root.lastSegment();
	}

	public IPath getRoot() {
		return root;
	}
}

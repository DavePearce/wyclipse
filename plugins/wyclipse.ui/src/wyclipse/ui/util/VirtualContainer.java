package wyclipse.ui.util;

import org.eclipse.jface.viewers.TreeNode;

import wybs.lang.Path;
import wybs.util.Trie;

/**
 * <p>
 * Represents a "virtual" container. This is a container which doesn't really
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
public class VirtualContainer {
	private java.io.File location;
	private Resource root;
	
	public VirtualContainer(java.io.File location) {
		this.location = location;
		root = new Resource(Trie.ROOT);
	}
	
	public Resource getRoot() {
		return root;
	}
	
	public class Resource extends TreeNode {
		protected Path.ID id;
		private Resource[] resources;
		
		private Resource(Path.ID id) {
			super(null);
			this.id = id;
		}
		
		public Path.ID getID() {
			return id;
		}
		
		@Override
		public Resource[] getChildren() {
			if (resources == null) {
				java.io.File myLocation = new java.io.File(location, id
						.toString().replace('/', java.io.File.separatorChar));

				if (myLocation.exists() && myLocation.isDirectory()) {
					java.io.File[] files = myLocation.listFiles();
					resources = new Resource[files.length];
					for (int i = 0; i != files.length; ++i) {
						Path.ID fid = id.append(files[i].getName());
						resources[i] = new Resource(fid);
					}
				}
			}
			return resources;
		}
	}
}

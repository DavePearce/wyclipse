package wyclipse.ui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * <p>
 * Represents a "virtual" project. This is a container which may not really
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
public class VirtualProject {

	/**
	 * The name of the project
	 */
	private final String name;

	/**
	 * The absolute path to this project somewhere in the system (typically, the
	 * file system). Observe that this will be null in the case of a truly
	 * "virtual" project (i.e. one which has not yet been created).
	 */
	private final IPath location;

	/**
	 * Get the root folder associated with this project.
	 */
	private final Folder root;

	public VirtualProject(String name, IPath location) {
		this.name = name;
		this.location = location;
		this.root = new Folder(Path.ROOT);
	}

	/**
	 * Get the name of this Virtual Project.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the physical location of this Virtual Project.
	 * 
	 * @return
	 */
	public IPath getLocation() {
		return location;
	}
	
	/**
	 * Return the root folder of this virtual project.
	 * 
	 * @return
	 */
	public Folder getFolder() {
		return root;
	}

	/**
	 * Create a folder with a given (relative) path, if it does not already
	 * exist.
	 * 
	 * @param path
	 */
	public void createFolder(IPath path) {
		Folder folder = root;

		// First, descend the appropriate path through the folder tree until we
		// either reach the folder we're looking for (in which case we just
		// return), or we find can no longer find matching a folder.
		List<Folder> children = folder.getChildren();
		int lastSegment = 1;
		for (int i = 0; i != children.size();) {
			Folder child = children.get(i);
			if(folder.path.equals(path)) {
				// Found the actual folder we're trying to create, so stop! 
				return;
			} else if (child.path.isPrefixOf(path)) {
				// Matched a sub-folder, so continue looking into it.
				i = 0;
				folder = child;
				children = folder.getChildren();
				lastSegment = lastSegment + 1;
			} else {
				// Sub-folder did not match, so continue to examine next child.
				i = i + 1;
			}
		}

		// If we get here, it means that the path doesn't exists and, hence, we
		// need to make it.
		String[] segments = path.segments();
		for (int i = lastSegment; i < segments.length; ++i) {
			Folder newFolder = new Folder(path.uptoSegment(i));
			folder.getChildren().add(newFolder);
			folder = newFolder;
		}
	}

	public class Folder {

		/**
		 * The path which this folder represents. Observe that this is stored
		 * relative to the enclosing project's location.
		 */
		private IPath path;

		/**
		 * A list of child nodes (i.e. sub-folders or sub-directories) for this
		 * node. Note, this may be null.
		 */
		private ArrayList<Folder> children;

		/**
		 * Create a folder with a given path relative to the enclosing project.
		 * 
		 * @param path
		 */
		public Folder(IPath path) {
			this.path = path;
		}

		/**
		 * Get path of this folder relative to the enclosing virtual project.
		 * 
		 * @return
		 */
		public IPath getPath() {
			return path;
		}
		
		/**
		 * Get the subfolders accessible from this folder. In the case of a
		 * non-virtual project (i.e. an existing project with a physical
		 * location), then we look to see what files are already there.
		 * 
		 * @return
		 */
		public List<Folder> getChildren() {
			if (children == null) {
				children = new ArrayList<Folder>();
				if (location != null) {
					// non-virtual node
					File dir = location.append(path).toFile();
					if (dir.exists()) {
						File[] contents = dir.listFiles();
						for (File f : contents) {
							if (f.isDirectory()) {
								children.add(new Folder(
										path.append(f.getName())));
							}
						}
					}
				}
			}
			return children;
		}
	}
}

package wyclipse.ui.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

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
 * it may not).  The user can then select items from this virtual container
 * and/or create new items.   
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class VirtualContainer {
	private java.io.File location;
	private Folder root;
	
	public VirtualContainer(java.io.File location) {
		this.location = location;
		root = new Folder(Trie.ROOT);
	}
	
	public Folder getRoot() {
		return root;
	}
	
	public static abstract class Resource {
		protected Path.ID id;
		
		private Resource(Path.ID id) {
			this.id = id;
		}
		
		public Path.ID getID() {
			return id;
		}
	}
	
	public class Folder extends Resource {

		private ArrayList<Resource> resources;
		
		private Folder(Path.ID id) {
			super(id);
		}
		
		public List<Resource> getResources() {
			if(resources == null) {
				populate();
			}
			return resources;
		}
		
		private void populate() {
			resources = new ArrayList<Resource>();
			java.io.File myLocation = new java.io.File(location, id.toString()
					.replace('/', java.io.File.separatorChar));

			if (myLocation.exists() && myLocation.isDirectory()) {
				for (java.io.File f : myLocation.listFiles()) {
					Path.ID fid = id.append(f.getName());
					if (f.isDirectory()) {
						resources.add(new Folder(fid));
					} else {
						resources.add(new File(fid));
					}
				}
			}
		}
	}
	
	public static class File extends Resource {
		private File(Path.ID id) {
			super(id);
		}
	}
}

package wyclipse.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import wybs.lang.Path;
import wybs.util.Trie;
import wyclipse.core.builder.WhileyPath;
import wyclipse.ui.Activator;

/**
 * Provides a tree viewer for items on the WhileyPath, along with appropriate
 * icons etc. This viewer is used, for example, by the
 * <code>NewWhileyProjectWizard</code> as part of the page where users configure
 * the build settings.
 * 
 * @author David J. Pearce
 * 
 */
public class WhileyPathViewer extends TreeViewer {
		
	/**
	 * Creates a whileypath viewer on a newly-created tree control under the
	 * given parent. The tree control is created using the given SWT style bits.
	 * 
	 * @param parent
	 * @param style
	 */
	public WhileyPathViewer(Composite parent, int style) {
		super(parent, style);
		setContentProvider(new ContentProvider());
		setLabelProvider(new LabelProvider());
	}

	/**
	 * The content provider is responsible for deconstructing the object being
	 * viewed in the viewer, so that the <code>TreeViewer</code> can navigate
	 * them. In this case, this means it deconstructs those items on the
	 * WhileyPath.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	protected final static class ContentProvider implements ITreeContentProvider {

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
			return getChildren(inputElement);
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof WhileyPath) {
				return whileyPath2Nodes((WhileyPath) parentElement);
			} else if (parentElement instanceof PathNode) {
				PathNode node = (PathNode) parentElement;
				return node.children;				
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
			if(element instanceof WhileyPath) {
				return true;
			} else if(element instanceof PathNode) {
				PathNode pn = (PathNode) element;
				return pn.children.length > 0;
			}
			return false;
		}		
	}
	
	private static enum PathKind {
		BUILD_RULE,
		FOLDER,
		INCLUDES,
		LIBRARY
	}
	
	private static class PathNode {
		public PathKind kind;
		public String text;
		public PathNode[] children;
		
		public PathNode(PathKind kind, String text, PathNode... children) {
			this.kind = kind;
			this.text = text;
			this.children = children;
		}
		
		public PathNode(PathKind kind, String text, Collection<PathNode> children) {
			this.kind = kind;
			this.text = text;
			this.children = new PathNode[children.size()];
			int i = 0;
			for(PathNode n : children) {
				this.children[i++] = n;
			}
		}
	}
	
	private static Object[] whileyPath2Nodes(WhileyPath whileypath) {
		List<WhileyPath.Entry> wpEntries = whileypath.getEntries();
		Object[] entries = new Object[wpEntries.size()];
		for (int i = 0; i != entries.length; ++i) {
			WhileyPath.Entry e = wpEntries.get(i);
			PathNode pn;
			if (e instanceof WhileyPath.BuildRule) {
				WhileyPath.BuildRule br = (WhileyPath.BuildRule) e;
				ArrayList<PathNode> nodes = new ArrayList<PathNode>();
				nodes.add(new PathNode(PathKind.INCLUDES,"Includes: " + br.getSourceIncludes()));
				nodes.add(new PathNode(PathKind.INCLUDES,"Output Folder: " + br.getOutputFolder()));
				nodes.add(new PathNode(PathKind.INCLUDES,"Verification: " + br.getEnableVerification()));
				nodes.add(new PathNode(PathKind.INCLUDES,"Runtime Assertions: " + br.getEnableRuntimeAssertions()));
				nodes.add(new PathNode(PathKind.INCLUDES,"Generate WyIL: " + br.getGenerateWyIL()));
				nodes.add(new PathNode(PathKind.INCLUDES,"Generate WyAL: " + br.getGenerateWyAL()));
				pn = new PathNode(PathKind.FOLDER, br.getSourceFolder()
						.toString(),nodes);				
			} else {
				WhileyPath.ExternalLibrary wl = (WhileyPath.ExternalLibrary) e;				
				ArrayList<PathNode> nodes = new ArrayList<PathNode>();
				nodes.add(new PathNode(PathKind.INCLUDES,"includes: " + wl.getIncludes()));
				pn = new PathNode(PathKind.LIBRARY, wl.getLocation().toString(),nodes);
			}
			entries[i] = pn;
		}
		return entries;
	}
		
	/**
	 * The label provider is responsible for associating labels with the objects
	 * being viewed in the viewer; in this case, that means it associates labels
	 * with items on the WhileyPath.
	 * 
	 * @author David J. Pearce
	 * 
	 */
	protected static class LabelProvider implements ILabelProvider {
		
		@Override
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void dispose() {			
			
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
			ImageDescriptor descriptor = null;
			
			if(element instanceof PathNode) {
				PathNode pn = (PathNode) element;
				switch(pn.kind) {
				case FOLDER:
					descriptor = Activator.getImageDescriptor("whiley_modulefolder.gif");
					break;
				case LIBRARY:
					descriptor = Activator.getImageDescriptor("jar_obj.gif");
					break;
				default:
					return null;
				}				
			} else {
				return null;
			}
		
			// TODO: use an image cache??
			return descriptor.createImage();							
		}		

		@Override
		public String getText(Object element) {
			if (element instanceof PathNode) {
				PathNode node = (PathNode) element;
				return node.text;
			} 
			return null;
		}
		
	}
}

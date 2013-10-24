package wyclipse.ui.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

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
				WhileyPath whileyPath = (WhileyPath) parentElement;
				java.util.List<WhileyPath.Entry> entries = whileyPath
						.getEntries();
				return entries.toArray(new Object[entries.size()]);
			} else if (parentElement instanceof WhileyPath.BuildRule) {
				WhileyPath.BuildRule rule = (WhileyPath.BuildRule) parentElement;
				Object[] entries = new Object[2];
				entries[0] = new SourceIncludesElement(rule.getSourceIncludes());
				entries[1] = new OutputFolderElement(rule.getOutputFolder());
				return entries;
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
			return element instanceof WhileyPath
					|| element instanceof WhileyPath.BuildRule;
		}		
	}
	
	private static class SourceIncludesElement {
		public String includes;
		
		public SourceIncludesElement(String includes) {
			this.includes = includes;
		}
	}
	
	private static class OutputFolderElement {
		public IPath folder;
		
		public OutputFolderElement(IPath folder) {
			this.folder = folder;
		}
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
			
			if(element instanceof WhileyPath.BuildRule) { 
				descriptor = Activator.getImageDescriptor("whiley_modulefolder.gif");
			} else {
				return null;
			}
		
			// TODO: use an image cache??
			return descriptor.createImage();							
		}		

		@Override
		public String getText(Object element) {
			if (element instanceof WhileyPath.BuildRule) {
				WhileyPath.BuildRule container = (WhileyPath.BuildRule) element;
				return container.getSourceFolder().toString();
			} else if (element instanceof SourceIncludesElement) {
				SourceIncludesElement sie = (SourceIncludesElement) element;
				return "Includes: " + sie.includes;
			} else if (element instanceof OutputFolderElement) {
				OutputFolderElement ofe = (OutputFolderElement) element;
				return "Output Folder: " + ofe.folder;
			} 
			return null;
		}
		
	}
}

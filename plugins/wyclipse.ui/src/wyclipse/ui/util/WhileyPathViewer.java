package wyclipse.ui.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

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
			
			descriptor = Activator.getImageDescriptor("whiley_modulefolder.gif");			
		
			// TODO: use an image cache??
			return descriptor.createImage();							
		}		

		@Override
		public String getText(Object element) {
			if (element instanceof WhileyPath.Container) {
				WhileyPath.Container container = (WhileyPath.Container) element;
				return container.getLocation().toString();
			}
			return null;
		}
		
	}
}

package wyclipse.jdt;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Opens the properties dialog for configuring the Whiley Build Path. When
 * applied to a project which does not include the <code>WhileyNature</code>,
 * then this is automatically added an initialised appropriately. Specifically,
 * this will be configured based on the ".whileypath" file (if one exists), or
 * will fall back to a default setup.
 * 
 * @author David J. Pearce
 * 
 */
public class ConfigureWhileyBuildPath implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("*** ConfigureWhileyBuildPath action called");
		
		// First, determine whether this command was executed on a selected
		// project or not.
		ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event)
				.getActivePage().getSelection();
		if (selection != null & selection instanceof IStructuredSelection) {
			IStructuredSelection iss = (IStructuredSelection) selection;
			Object firstElement = iss.getFirstElement();
			if (firstElement instanceof IProject) {
				IProject project = (IProject) firstElement;
				System.out.println("*** CALLED ON IPROJECT "
						+ project.getName());
			}
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) { }

}

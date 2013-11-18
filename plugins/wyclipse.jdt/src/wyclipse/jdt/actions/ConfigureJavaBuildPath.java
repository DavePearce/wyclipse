package wyclipse.jdt.actions;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.handlers.HandlerUtil;

import org.eclipse.jdt.core.*;

/**
 * <p>
 * Responsible for configuring an existing Whiley project as a Java Project
 * (i.e. it adds the Java Nature to the project). If the project already has the
 * Java Nature, then this project simply opens the Java Build Path properties
 * dialog.
 * </p>
 * 
 * <p>
 * <b>NOTE:</b> Since this plugin interfaces with the JDT, it is dependent on
 * some of the JDT plugins (e.g. org.eclipse.jdt.core).
 * </p>
 * 
 * @author David J. Pearce
 * 
 */
public class ConfigureJavaBuildPath implements IHandler {

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	@Override
	public void dispose() {}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("*** ConfigureJavaBuildPath action called");

		// First, determine whether this command was executed on a selected
		// project or not.

		try {
			IWorkbenchWindow activeWorkbenchWindow = HandlerUtil
					.getActiveWorkbenchWindow(event);
			ISelection selection = activeWorkbenchWindow.getActivePage()
					.getSelection();
			if (selection != null & selection instanceof IStructuredSelection) {
				IStructuredSelection iss = (IStructuredSelection) selection;
				Object firstElement = iss.getFirstElement();
				if (firstElement instanceof IProject) {

					IProject project = (IProject) firstElement;					
					if (hasJavaNature(project)) {
						// Ok, project already has Whiley nature so jump
						// straight to the Whiley Build Path Configuration page.
						openPropertiesDialog(project,activeWorkbenchWindow);
					} else {
						System.out
								.println("*** PROJECT DOESN'T HAVE WHILEY NATURE");
					}
				}
			}
		} catch (CoreException e) {
			throw new ExecutionException(
					"A problem occurred configuring the Whiley Build Path", e);
		}

		return null;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) { }

	/**
	 * Determine whether a given project has the Java nature associated with
	 * it or not. This is important because, if it does, then we can go straight
	 * to the Whiley Build Path property page. Otherwise, we need to use a
	 * wizard.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	private boolean hasJavaNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		for (String natureId : description.getNatureIds()) {
			if (natureId.equals(org.eclipse.jdt.core.JavaCore.NATURE_ID)) {
				return true;

			}
		}
		return false;
	}
	
	/**
	 * Force the Whiley Build Path property page to open in the properties
	 * Dialog.
	 * 
	 * @param project
	 * @param window
	 */
	private void openPropertiesDialog(IProject project, IWorkbenchWindow window) {
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(
				window.getShell(), project,
				JavaCore.NATURE_ID,
				new String[] { JavaCore.NATURE_ID },
				Collections.EMPTY_MAP);
		dialog.open();
	}
}

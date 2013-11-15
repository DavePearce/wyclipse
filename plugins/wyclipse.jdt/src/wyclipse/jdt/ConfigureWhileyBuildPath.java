package wyclipse.jdt;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

/**
 * Responsible for adding the <code>WhileyNature</code> onto an existing
 * project. This requires configuring the build path based on the existing code
 * structure.
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

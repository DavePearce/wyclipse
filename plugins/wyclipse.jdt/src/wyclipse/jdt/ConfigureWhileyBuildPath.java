package wyclipse.jdt;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

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

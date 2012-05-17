package wyclipse.builder;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

/**
 * Responsible for tunning a core exception through as an IOException
 * 
 * @author David J. Pearce
 * 
 */
public class CoreIOException extends IOException {
	public final CoreException payload;
	
	public CoreIOException(CoreException payload) {
		this.payload = payload;
	}
}

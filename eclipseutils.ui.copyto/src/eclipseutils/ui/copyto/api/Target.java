package eclipseutils.ui.copyto.api;

import org.eclipse.core.runtime.IProgressMonitor;

public interface Target {

	/**
	 * Copies a Copyable to this target.
	 * 
	 * @param copyable
	 * @param monitor
	 * @return
	 */
	String copy(Copyable copyable, IProgressMonitor monitor);
}

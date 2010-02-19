package copyto.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.prefs.Preferences;

public interface Target {

	/**
	 * Copies a Copyable to this target.
	 * 
	 * @param copyable
	 * @param monitor
	 * @return
	 */
	Results copy(final IProgressMonitor monitor, final Copyable... copyables);

	String getName();

	String getId();

	String getUrl();

	void save(Preferences node);
}

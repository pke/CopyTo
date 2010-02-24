package copyto.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * A targets post paramters are saved in a Map<String, Object>
 * 
 * The following SWT controls are created for each type of Object:
 * 
 * <pre>
 * StringParam  - Text
 * BooleanParam - Button(SWT.PUSH), it also 
 * MapParam     - Combo(SWT.LIST)
 * </pre>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
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

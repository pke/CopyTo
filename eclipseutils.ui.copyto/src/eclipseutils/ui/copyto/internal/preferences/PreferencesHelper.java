package eclipseutils.ui.copyto.internal.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Helper for working with preferences.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public final class PreferencesHelper {

	public static Map<String, String> mergeParams(String id,
			Map<String, String> source) {

		Map<String, String> result = new HashMap<String, String>(source.size());
		result.putAll(source);

		IEclipsePreferences node = new InstanceScope().getNode(FrameworkUtil
				.getBundle(PreferencesHelper.class).getSymbolicName());
		try {
			String nodePath = "targets/" + id;
			if (node.nodeExists(nodePath)) {
				Preferences preferences = node.node(nodePath);
				for (String key : preferences.keys()) {
					result.put(key, preferences.get(key, source.get(key)));
				}
			}
		} catch (BackingStoreException e) {
		}
		return result;
	}

	private PreferencesHelper() {
	}
}

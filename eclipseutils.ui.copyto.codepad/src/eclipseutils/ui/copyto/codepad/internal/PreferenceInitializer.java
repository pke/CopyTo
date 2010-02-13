package eclipseutils.ui.copyto.codepad.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		final Preferences node = new ConfigurationScope()
				.getNode(FrameworkUtil.getBundle(getClass()).getSymbolicName());
		String symbolicName = "codepad.org";
		String firstRun = symbolicName + ".firstRun";
		if (node.getBoolean(firstRun, true)) {
			node.putBoolean(firstRun, false);
			final Preferences preferences = node
					.node("targets/" + symbolicName);
			preferences
					.put(
							"url",
							"http://codepad.org/?submit=Submit&code=${copyto.text}&lang=${codepad.lang:${copyto.mime-type}}");
			preferences.put("label", "codepad.org");
			try {
				node.flush();
			} catch (final BackingStoreException e) {
			}
		}
	}
}

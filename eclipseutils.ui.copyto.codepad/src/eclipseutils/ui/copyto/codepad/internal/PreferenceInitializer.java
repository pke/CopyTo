package eclipseutils.ui.copyto.codepad.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), "copyto");
		preferenceStore.putValue("url", "http://codepad.org/submit=Submit&code=${copyto.text}&lang=${codepad.lang:${copyto.mime-type}}");
		preferenceStore.putValue("id", "copyto.codepad.org");
	}

}

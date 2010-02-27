package copyto.core;

import org.osgi.service.prefs.Preferences;

public interface Persistable {
	void load(Preferences preferences);

	void save(Preferences preferences);
}

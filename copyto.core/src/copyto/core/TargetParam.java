package copyto.core;

import org.osgi.service.prefs.Preferences;

public interface TargetParam<T> {
	String getName();

	void setName(String name);

	T getValue();

	void setValue(T value);

	String getLabelValue();

	boolean isReadOnly();

	void setReadOnly(boolean value);

	void save(Preferences preferences);

	void load(Preferences preferences);
}

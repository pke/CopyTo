package copyto.core.models;

import org.osgi.service.prefs.Preferences;

public class StringTargetParamModel extends AbstractTargetParamModel<String> {

	public StringTargetParamModel(Preferences preferences) {
		super(preferences);
	}

	public StringTargetParamModel(String name, String value) {
		super(name, value);
	}

	@Override
	protected void doLoad(Preferences preferences) {
		setValue(preferences.get("value", getValue()));
	}

	public void doSave(Preferences preferences) {
		preferences.put("value", getValue());
	}
	
	@Override
	protected String getId() {
		return "string";
	}
}

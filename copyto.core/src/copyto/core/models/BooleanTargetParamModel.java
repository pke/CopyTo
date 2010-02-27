package copyto.core.models;

import org.osgi.service.prefs.Preferences;

import copyto.core.TargetBooleanParam;

public class BooleanTargetParamModel extends AbstractTargetParamModel<Boolean> implements TargetBooleanParam {

	private String stringValue;

	public BooleanTargetParamModel(Preferences preferences) {
		super(preferences);
	}

	public BooleanTargetParamModel(String name, Boolean value,
			String stringValue) {
		super(name, value);
		this.stringValue = stringValue;
	}
	
	@Override
	protected String getId() {
		return "boolean";
	}

	@Override
	public String getLabelValue() {
		return getValue() ? stringValue : "";
	}

	public void setStringValue(String stringValue) {
		firePropertyChange("stringValue", this.stringValue,
				this.stringValue = stringValue);
	}

	protected void doLoad(Preferences preferences) {
		setValue(preferences.getBoolean("value", getValue()));
		setStringValue(preferences.get("stringValue", stringValue));
	}

	public void doSave(Preferences preferences) {
		preferences.putBoolean("value", getValue());
		preferences.put("stringValue", stringValue);
	}
}
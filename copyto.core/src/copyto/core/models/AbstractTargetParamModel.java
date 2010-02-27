package copyto.core.models;

import org.osgi.service.prefs.Preferences;

import copyto.core.TargetParam;

public abstract class AbstractTargetParamModel<T> extends AbstractModel
		implements TargetParam<T> {
	private String name;
	private T value;
	private boolean readOnly;

	public AbstractTargetParamModel(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public AbstractTargetParamModel(Preferences preferences) {
		load(preferences);
	}

	public final void load(Preferences preferences) {
		setName(preferences.name());
		doLoad(preferences.node(getId()));
	}
	
	public final void save(Preferences preferences) {
		doSave(preferences.node(getId()));
	}

	protected abstract String getId();
	
	protected abstract void doLoad(Preferences preferences);
	protected abstract void doSave(Preferences preferences);

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		String labelValue = getLabelValue();
		firePropertyChange("value", this.value, this.value = value);
		firePropertyChange("labelValue", labelValue, getLabelValue());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
	}
	
	public String getLabelValue() {
		return getValue() != null ? getValue().toString() : "";
	}
	
	public boolean isReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean value) {
		firePropertyChange("readOnly", this.readOnly, this.readOnly = value);
	}
}

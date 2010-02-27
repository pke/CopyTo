package copyto.core;

import copyto.core.models.AbstractModel;

public class NamedValue extends AbstractModel {

	private String name;
	private String value;

	public NamedValue(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		firePropertyChange("name", this.name, this.name = name);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		firePropertyChange("value", this.value, this.value = value);
	}

}
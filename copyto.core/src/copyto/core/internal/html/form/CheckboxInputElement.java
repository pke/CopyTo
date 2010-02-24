package copyto.core.internal.html.form;

import org.w3c.dom.Node;

public class CheckboxInputElement extends InputElement {

	private boolean selected;

	public CheckboxInputElement(Node node) {
		super(node);
		this.selected = "checked".equals(getAttribute("checked", "")); 
	}
	
	boolean isSelected() {
		return selected;
	}
}

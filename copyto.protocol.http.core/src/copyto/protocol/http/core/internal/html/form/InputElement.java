package copyto.protocol.http.core.internal.html.form;

import org.w3c.dom.Node;

public class InputElement extends HtmlElement {

	public InputElement(Node node) {
		super(node);
	}

	public String getValue() {
		return getAttribute("value", "");
	}

	@Override
	public String toString() {
		return String.format("%s [name=%s, value=%s]", getAttribute("type",
				getName()), getName(), getValue());
	}
}

package copyto.core.internal.html.form;

import org.w3c.dom.Node;

public class HtmlElement {
	private final Node node;

	public HtmlElement(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	static public String getAttribute(Node node, String name,
			String defaultValue) {
		Node attribute = node.getAttributes().getNamedItem(name);
		return attribute != null ? attribute.getNodeValue() : defaultValue;
	}

	public String getAttribute(String name, String defaultValue) {
		return getAttribute(node, name, defaultValue);
	}

	public String getName() {
		return getAttribute("name", null);
	}
	
	@Override
	public String toString() {
		return String.format("%s [name=%s]", node.getNodeName(), getName());
	}
}
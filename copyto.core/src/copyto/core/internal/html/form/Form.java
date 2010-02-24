package copyto.core.internal.html.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Form extends HtmlElement {

	private static XPathExpression elementsPath;
	private Collection<HtmlElement> items;

	public Form(Node node) {
		super(node);
		if (elementsPath == null) {
			try {
				elementsPath = XPathFactory.newInstance().newXPath().compile(
						".//input | .//select | .//textarea");
			} catch (XPathExpressionException e) {
			}
		}
	}

	public Collection<HtmlElement> getElements() {
		if (items == null) {
			try {
				NodeList elements = (NodeList) elementsPath.evaluate(getNode(),
						XPathConstants.NODESET);
				items = new ArrayList<HtmlElement>(elements.getLength());
				for (int e = 0; e < elements.getLength(); ++e) {
					Node element = elements.item(e);
					String name = element.getNodeName();
					if ("select".equals(name)) {
						SelectElement selectFormElement = new SelectElement(
								element);
						items.add(selectFormElement);
					} else if ("input".equals(name)) {
						String type = HtmlElement.getAttribute(element, "type",
								null);
						if ("hidden".equals(type)) {
							items.add(new HiddenInputElement(element));
						} else if ("text".equals(type)) {
							items.add(new TextInputElement(element));
						} else if ("checkbox".equals(type)) {
							items.add(new CheckboxInputElement(element));
						}
					} else if ("textarea".equals(name)) {
						items.add(new TextAreaElement(element));
					}
				}
			} catch (XPathExpressionException e) {
			}

		}
		return Collections.unmodifiableCollection(items);
	}

	public String getAction() {
		return getAttribute("action", "");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(" action=");
		sb.append(getAction());
		sb.append('\n');
		for (HtmlElement element : getElements()) {
			sb.append("  " + element.toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
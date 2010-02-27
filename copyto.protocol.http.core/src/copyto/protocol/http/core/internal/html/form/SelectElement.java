package copyto.protocol.http.core.internal.html.form;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SelectElement extends HtmlElement {
	private Map<String, String> items;

	public SelectElement(Node node) {
		super(node);
	}

	public Map<String, String> getOptions() {
		if (null == items) {
			try {
				XPathExpression path = XPathFactory.newInstance().newXPath()
						.compile("option");
				NodeList options = (NodeList) path.evaluate(getNode(),
						XPathConstants.NODESET);
				items = new LinkedHashMap<String, String>(options.getLength());
				for (int i = 0; i < options.getLength(); ++i) {
					Node option = options.item(i);
					String name = getAttribute(option, "value", "");
					String value = option.getTextContent();
					items.put(name, value);
				}
			} catch (XPathExpressionException e) {
			}
		}
		return Collections.unmodifiableMap(items);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append('\n');
		String selected = getSelected();		
		for (Entry<String, String > pair : getOptions().entrySet()) {
			sb.append("    ");
			String key = pair.getKey();
			sb.append(key);
			sb.append('=');
			sb.append(pair.getValue());
			if (key.equals(selected)) {
				sb.append(" (selected)");
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	public String getSelected() {
		return getAttribute("selected", getOptions().keySet().iterator().next());
	}
}
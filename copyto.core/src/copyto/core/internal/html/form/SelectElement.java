package copyto.core.internal.html.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.NameValuePair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SelectElement extends HtmlElement {
	private Collection<NameValuePair> items;

	public SelectElement(Node node) {
		super(node);
	}

	public Collection<NameValuePair> getOptions() {
		if (null == items) {
			try {
				XPathExpression path = XPathFactory.newInstance().newXPath()
						.compile("option");
				NodeList options = (NodeList) path.evaluate(getNode(),
						XPathConstants.NODESET);
				items = new ArrayList<NameValuePair>(options.getLength());
				for (int i = 0; i < options.getLength(); ++i) {
					Node option = options.item(i);
					String name = getAttribute(option, "value", "");
					String value = option.getTextContent();
					items.add(new NameValuePair(name, value));
				}
			} catch (XPathExpressionException e) {
			}
		}
		return Collections.unmodifiableCollection(items);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append('\n');
		for (NameValuePair pair : getOptions()) {
			sb.append("    ");
			sb.append(pair.getName());
			sb.append('=');
			sb.append(pair.getValue());
			sb.append('\n');
		}
		return sb.toString();
	}
}
package copyto.core.internal.html.form;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FormParser {
	private XPathExpression formPath;

	public FormParser() {
		try {
			formPath = XPathFactory.newInstance().newXPath().compile("//form");
		} catch (Exception e) {
		}
	}

	public Collection<Form> parse(URL url) {
		InputStream stream = null;
		try {
			try {
				stream = url.openStream();
				return parse(stream);
			} catch (IOException e) {
			}
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
			}
		}
		return Collections.emptyList();
	}

	public Collection<Form> parse(InputStream is) {
		InputSource source = new InputSource(is);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});

			Document document = db.parse(source);
			NodeList nodes = (NodeList) formPath.evaluate(document
					.getDocumentElement(), XPathConstants.NODESET);
			Collection<Form> forms = new ArrayList<Form>(nodes.getLength());
			for (int i = 0; i < nodes.getLength(); ++i) {
				Node node = nodes.item(i);
				forms.add(new Form(node));
			}
			return forms;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}
}
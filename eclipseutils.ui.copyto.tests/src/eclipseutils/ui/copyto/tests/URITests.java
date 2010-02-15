package eclipseutils.ui.copyto.tests;


import java.util.Collection;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.Test;
import org.apache.commons.httpclient.util.ParameterParser;

public class URITests {

	@SuppressWarnings("unchecked")
	@Test
	public void parseURI() throws Exception {
		PostMethod method = new PostMethod();
		URI uri = new URI("http://pastebin.com/pastebin.php?code2=${copyto.text}&paste=Send&format=${pastebin.format:${copyto.mime-type}}", false);
		method.setURI(uri);
		Collection<NameValuePair> parse = new ParameterParser().parse(uri.getQuery(), '=');
		
				
		String path = uri.getPath();
		String scheme = uri.getScheme();
		String query = uri.getQuery();
	}
}

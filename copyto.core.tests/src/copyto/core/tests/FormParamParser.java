package copyto.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.junit.Test;

import copyto.core.internal.html.form.Form;
import copyto.core.internal.html.form.FormParser;
import copyto.core.internal.html.form.HtmlElement;

public class FormParamParser {

	static private String oneFormValidHtml = "<html><body><div>"
			+ "<form action=\"/post.php\" name=\"paste_form\">"
			+ "<textarea cols=\"5\" rows=\"5\" name=\"paste_code\" class=\"paste_textarea\"></textarea>"
			+ "<select name=\"paste_expire_date\">"
			+ "<option value=\"N\">Never</option><option value=\"10M\">10 Minutes</option><option value=\"1H\">1 Hour</option><option value=\"1D\">1 Day</option><option value=\"1M\">1 Month</option>"
			+ "</select>" + "</form>" + "</div></body></html>";

	@Test
	public void parseOneFormValid() {
		Collection<Form> forms = new FormParser()
				.parse(new ByteArrayInputStream(oneFormValidHtml.getBytes()));
		assertEquals(1, forms.size());
		Form form = forms.iterator().next();
		assertNotNull(form);
		Collection<HtmlElement> elements = form.getElements();
		assertEquals(2, elements.size());
	}

	@Test
	public void parsePasteBinCom() {
		try {
			URL url = new URL("http://www.pastebin.com");
			Collection<Form> forms = new FormParser().parse(url);
			for (Form form : forms) {
				System.out.println(form);
			}
			assertEquals(2, forms.size());
		} catch (MalformedURLException e) {
		}
	}

	@Test
	public void parseCodepadOrg() {
		try {
			URL url = new URL("http://www.codepad.org");
			Collection<Form> forms = new FormParser().parse(url);
			for (Form form : forms) {
				System.out.println(form);
			}
			assertEquals(1, forms.size());
		} catch (MalformedURLException e) {
		}
	}
}

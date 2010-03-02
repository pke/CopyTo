package copyto.target.http.core.internal.responses;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.httpclient.HttpMethod;
import org.osgi.service.prefs.Preferences;

import copyto.core.Persistable;
import copyto.core.models.AbstractModel;
import copyto.target.http.core.ResponseHandler;

public class BodyResponseHandler extends AbstractModel implements ResponseHandler, Persistable {

	private Pattern regex;

	public URL getLocation(HttpMethod method) throws Exception {
		String body = method.getResponseBodyAsString();
		
		if (regex == null) {
			try {
				return new URL(body);
			} catch (MalformedURLException e) {
				return new URL("http://" + method.getRequestHeader("Host").getValue() + body);
			}
		}
		Matcher matcher = regex.matcher(body);
		if (matcher.matches()) {
			return new URL(matcher.group());
		}
		throw new IllegalArgumentException("No URL found in response");
	}

	public void load(Preferences preferences) {
		setRegex(preferences.get("regex", null));
	}

	public void save(Preferences preferences) {
		if (null == regex) {
			preferences.remove("regex");
		} else {
			preferences.put("regex", regex.toString());
		}
	}
	
	public String getRegex() {
		return regex != null ? regex.toString() : "";
	}
	
	public void setRegex(String regex) {
		try {
			if (regex != null) {
				firePropertyChange("regex", this.regex, this.regex = Pattern.compile(regex));
			} else {
				firePropertyChange("regex", this.regex, this.regex = null);
			}
		} catch (PatternSyntaxException e) {
		}
	}
}
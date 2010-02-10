package eclipseutils.ui.copyto.pastebin.com.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.commands.IParameterValues;

public class FormatValues implements IParameterValues {
	static Properties formats;

	public Map<?, ?> getParameterValues() {
		if (formats == null) {
			formats = new Properties();
			try {
				final InputStream resourceAsStream = getClass()
						.getResourceAsStream("FormatValues.properties"); //$NON-NLS-1$
				if (resourceAsStream != null) {
					formats.load(resourceAsStream);
				}
			} catch (final IOException e) {
			}
		}

		return formats;
	}

}

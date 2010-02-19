package copyto.target.pastebin.com.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.commands.IParameterValues;

/**
 * Reads a list of parameter/values from the <code>FormatValus.properties</code> file.
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
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

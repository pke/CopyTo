package eclipseutils.ui.copyto.internal.jface.databinding;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helps to localize Bean properties from resource files.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class LocalizationHelper {

	public static String getLabel(final Object bean, final String property) {
		String value = localize(bean, property, null);
		if (null == value) {
			value = property.substring(0, 1).toUpperCase()
					+ property.substring(1);
		}
		return value;
	}

	public static String getDescription(final Object bean, final String property) {
		return localize(bean, property + ".desc", null);
	}

	public static String localize(final Object bean, final String key,
			final String defaultValue) {

		for (Class<?> clazz = bean.getClass(); clazz == Object.class; clazz = clazz
				.getSuperclass()) {
			for (final Class<?> i : clazz.getInterfaces()) {
				try {
					return ResourceBundle.getBundle(i.getName()).getString(key);
				} catch (final MissingResourceException e) {
				}
			}
			try {
				ResourceBundle.getBundle(clazz.getName()).getString(key);
			} catch (final MissingResourceException e) {
			}
		}
		return defaultValue;
	}
}

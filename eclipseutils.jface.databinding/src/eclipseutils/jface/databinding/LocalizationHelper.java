/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package eclipseutils.jface.databinding;

import java.io.Serializable;
import java.util.EventListener;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helps to localize Bean properties from resource files.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class LocalizationHelper {

	/**
	 * @param bean
	 * @param property
	 * @return the localized label text or the capitalized property.
	 */
	public static String getLabel(final Object bean, final String property) {
		String value = localize(bean, property, null);
		if (null == value) {
			value = property.substring(0, 1).toUpperCase()
					+ property.substring(1);
		}
		return value;
	}

	/**
	 * @param bean
	 * @param property
	 * @return the description text or <code>null</code>.
	 */
	public static String getDescription(final Object bean, final String property) {
		return localize(bean, property + ".desc", null); //$NON-NLS-1$
	}

	/**
	 * Searches the resource bundles of the given bean object for a key and
	 * returns its value.
	 * 
	 * <p>
	 * The method looks for resource bundles of the beans interfaces as well as
	 * up the class hierarchy of the bean itself.
	 * 
	 * @param bean
	 * @param key
	 * @param defaultValue
	 * @return a value for the given <i>key</i> or <i>defaultValue</i> if the
	 *         key was not found.
	 */
	public static String localize(final Object bean, final String key,
			final String defaultValue) {

		for (Class<?> clazz = bean.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			try {
				return localize(clazz, key);
			} catch (final MissingResourceException e) {				
			}
			
			for (final Class<?> i : clazz.getInterfaces()) {
				if (i == Serializable.class || i == EventListener.class) {
					continue;
				}
				try {
					return localize(i, key);
				} catch (final MissingResourceException e) {
				}
			}
		}
		return defaultValue;
	}

	private static String localize(Class<?> clazz, final String key) {
		return ResourceBundle.getBundle(clazz.getName(), Locale.getDefault(), clazz.getClassLoader()).getString(key);
	}
}

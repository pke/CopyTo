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

		for (Class<?> clazz = bean.getClass(); clazz != Object.class; clazz = clazz
				.getSuperclass()) {
			for (final Class<?> i : clazz.getInterfaces()) {
				if (i == Serializable.class || i == EventListener.class) {
					continue;
				}
				try {
					return ResourceBundle.getBundle(i.getName()).getString(key);
				} catch (final MissingResourceException e) {
				}
			}
			try {
				return ResourceBundle.getBundle(clazz.getName()).getString(key);
			} catch (final MissingResourceException e) {
			}
		}
		return defaultValue;
	}
}

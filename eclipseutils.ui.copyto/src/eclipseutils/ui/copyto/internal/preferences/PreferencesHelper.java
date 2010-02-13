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
package eclipseutils.ui.copyto.internal.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Helper for working with preferences.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class PreferencesHelper {

	public static Map<String, String> mergeParams(final String id,
			final Map<String, String> source) {

		final Map<String, String> result = new HashMap<String, String>(source
				.size());
		result.putAll(source);

		final IEclipsePreferences node = new InstanceScope()
				.getNode(FrameworkUtil.getBundle(PreferencesHelper.class)
						.getSymbolicName());
		try {
			final String nodePath = "targets/" + id;
			if (node.nodeExists(nodePath)) {
				final Preferences preferences = node.node(nodePath);
				for (final String key : preferences.keys()) {
					result.put(key, preferences.get(key, source.get(key)));
				}
			}
		} catch (final BackingStoreException e) {
		}
		return result;
	}

	private PreferencesHelper() {
	}
}

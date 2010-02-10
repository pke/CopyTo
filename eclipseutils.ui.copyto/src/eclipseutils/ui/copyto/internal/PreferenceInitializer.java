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
package eclipseutils.ui.copyto.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Initializes the preferences for the copyto plugin.
 * 
 * <p>
 * If there is only one copyto extension available, it also sets the last
 * executed contribution to this only one.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@SuppressWarnings("nls")
	@Override
	public void initializeDefaultPreferences() {
		final Preferences node = new InstanceScope().getNode(FrameworkUtil
				.getBundle(getClass()).getSymbolicName());
		if (!node.getBoolean("initialized", false)) {
			node.putBoolean("initialized", true);
			Preferences preferences = node.node("targets/copyto.pastebin.com");
			preferences
					.put(
							"url",
							"http://pastebin.com/pastebin.php?code2=${copyto.text}&paste=Send&format=${pastebin.format:${copyto.mime-type}}");
			preferences.put("label", "pastebin.com");
			try {
				node.flush();
			} catch (BackingStoreException e) {
			}
		}
	}
}

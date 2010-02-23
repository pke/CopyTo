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
package copyto.target.pastebin.com.internal;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@SuppressWarnings("nls")
	@Override
	public void initializeDefaultPreferences() {
		// This will give a node for our host bundle
		final Preferences node = new ConfigurationScope().getNode(FrameworkUtil.getBundle(getClass()).getSymbolicName());
		String symbolicName = "pastebin.com";
		String firstRun = symbolicName + ".firstRun";
		if (node.getBoolean(firstRun, true)) {
			node.putBoolean(firstRun, false);
			final Preferences preferences = node
					.node("targets/" + symbolicName);
			preferences
					.put(
							"url",
							"http://pastebin.com/post.php?paste_code=${copyto.text}&submit=Submit&paste_format=${pastebin.com.format:${copyto.mime-type}}");
			preferences.put("label", "pastebin.com");
			try {
				node.flush();
			} catch (final BackingStoreException e) {
			}
		}
	}
}

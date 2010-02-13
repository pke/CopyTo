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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class TargetFactoryImpl implements TargetService {

	private static final String QUALIFIER = FrameworkUtil.getBundle(
			Target.class).getSymbolicName()
			+ "/targets";

	public Target find(final String id) {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		try {
			if (preferences.nodeExists(id)) {
				return new Target(preferences.node(id), configurationScope
						.getLocation());
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public Target getLastSelected() {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		final String id = preferences.get("lastId", null);
		if (id != null) {
			return find(id);
		}
		return null;
	}

	public void setLastSelected(final String id) {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		if (id == null) {
			preferences.remove("lastId");
		} else {
			preferences.put("lastId", id);
		}
		final ICommandService cs = (ICommandService) PlatformUI.getWorkbench()
				.getService(ICommandService.class);
		cs.refreshElements(CopyToHandler.COMMAND_ID, null);
	}

	// TODO:: Add EH and iterate until at least one can be returned.
	public Target findFirst() {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		try {
			final String[] names = preferences.childrenNames();
			if (names.length > 0) {
				return new Target(preferences.node(names[0]),
						configurationScope.getLocation());
			}
		} catch (final BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int count() {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		try {
			return preferences.childrenNames().length;
		} catch (final BackingStoreException e) {
		}
		return 0;
	}

	public List<Target> findAll() {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		final List<Target> items = new ArrayList<Target>();
		try {
			for (final String name : preferences.childrenNames()) {
				final Target item = new Target(preferences.node(name),
						configurationScope.getLocation());
				if (item != null) {
					items.add(item);
				}
			}
		} catch (final BackingStoreException e) {
		}
		return items;
	}

	public void save(final Collection<Target> items) {
		final IScopeContext instanceScope = new ConfigurationScope();
		Preferences node = instanceScope.getNode(QUALIFIER);
		try {
			node.removeNode();
			node = instanceScope.getNode(QUALIFIER);
			for (final Target item : items) {
				item.save(node.node(item.getId()));
			}
			node.flush();

		} catch (final BackingStoreException e) {
		}

	}

}

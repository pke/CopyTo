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
package copyto.ui.internal.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import osgiutils.services.LogHelper;


import copyto.core.Target;
import copyto.core.TargetService;
import copyto.core.TargetServiceListener;
import copyto.ui.internal.commands.CopyToHandler;
import copyto.ui.internal.models.TargetModel;


/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class TargetServiceImpl implements TargetService {

	private static final String QUALIFIER = "copyto.core" + "/targets"; //$NON-NLS-1$

	/**
	 * 
	 */
	public TargetServiceImpl() {
		// This will trigger PreferenceInitializers to run
		final ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(
				new ConfigurationScope(), QUALIFIER);
		preferenceStore.getBoolean("test"); //$NON-NLS-1$
	}

	public Target find(final String id) {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		try {
			if (preferences.nodeExists(id)) {
				return new TargetModel(preferences.node(id), configurationScope
						.getLocation());
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public Target getLastSelected() {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		final String id = preferences.get("lastId", null); //$NON-NLS-1$
		if (id != null) {
			return find(id);
		}
		return null;
	}

	public void setLastSelected(final String id) {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		if (id == null) {
			preferences.remove("lastId"); //$NON-NLS-1$
		} else {
			preferences.put("lastId", id); //$NON-NLS-1$
		}
		try {
			preferences.flush();
		} catch (final BackingStoreException e) {
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
				return new TargetModel(preferences.node(names[0]),
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
				final Target item = new TargetModel(preferences.node(name),
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

		notifyListeners(items);
	}

	private void notifyListeners(final Collection<Target> items) {
		final BundleContext context = FrameworkUtil.getBundle(getClass())
				.getBundleContext();
		try {
			final ServiceReference[] references = context.getServiceReferences(
					TargetServiceListener.class.getName(), null);
			if (references != null) {
				for (final ServiceReference ref : references) {
					try {
						final TargetServiceListener listener = (TargetServiceListener) context
								.getService(ref);
						if (listener != null) {
							listener.targetsChanged(items);
						}
					} catch (final Throwable t) {
						LogHelper.error(t,
								"Error calling TargetServiceListener in %s", //$NON-NLS-1$
								ref.getBundle().getSymbolicName());
					} finally {
						context.ungetService(ref);
					}
				}
			}
		} catch (final InvalidSyntaxException e) {
		}
	}
}

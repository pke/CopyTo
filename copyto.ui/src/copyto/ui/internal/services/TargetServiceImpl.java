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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Services;
import copyto.core.TargetFactoryDescriptor;
import copyto.core.TargetFactories;
import copyto.core.Target;
import copyto.core.TargetDescriptor;
import copyto.core.TargetService;
import copyto.core.TargetServiceListener;
import copyto.ui.internal.commands.CopyToHandler;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class TargetServiceImpl implements TargetService {

	private static final String FACTORY = "factory"; //$NON-NLS-1$
	private static final String QUALIFIER = "copyto.core/targets"; //$NON-NLS-1$

	private class TargetDescriptorImpl implements TargetDescriptor {
		private final String id;
		private final TargetFactoryDescriptor factoryDesc;
		private final Preferences itemNode;

		private TargetDescriptorImpl(String id, TargetFactoryDescriptor desc,
				Preferences itemNode) {
			this.id = id;
			this.factoryDesc = desc;
			this.itemNode = itemNode;
		}

		public String getId() {
			return id;
		}

		public Target createTarget() {
			Target target = factoryDesc.getFactory().createTarget();
			target.load(itemNode);
			return target;
		}

		public String getLabel() {
			return itemNode.get("label", "<undefined>");
		}
	}

	
	/**
	 * 
	 */
	public TargetServiceImpl() {
		// This will trigger PreferenceInitializers to run
		/*final ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(
				new ConfigurationScope(), QUALIFIER);
		preferenceStore.getBoolean("test"); //$NON-NLS-1$*/
	}

	public OutputStream exportToStream(String id) {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER + "/id");
		if (preferences != null) {
			return Services.run(IPreferencesService.class, new ServiceRunnable<IPreferencesService, OutputStream>() {
				public OutputStream run(IPreferencesService service) {
					OutputStream output = new ByteArrayOutputStream();
					try {
						service.exportPreferences(preferences, output, null);
						return output;
					} catch (CoreException e) {
					}
					return null;
				}
			});
		}
		return null;
	}
	
	public void importFromStream(final InputStream is) {
		Services.run(IPreferencesService.class, new SimpleServiceRunnable<IPreferencesService>() {
			public void runWithService(IPreferencesService service) {
				try {
					service.importPreferences(is);
				} catch (CoreException e) {
				}
			}
		});
	}
	
	public TargetDescriptor find(final String id) {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		try {
			if (preferences.nodeExists(id)) {
				Preferences itemNode = preferences.node(id);
				final String protocolId = itemNode.get(FACTORY, null);
				if (protocolId != null) {
					final TargetFactoryDescriptor desc = Services
							.run(
									TargetFactories.class,
									new ServiceRunnable<TargetFactories, TargetFactoryDescriptor>() {
										public TargetFactoryDescriptor run(
												TargetFactories service) {
											return service.find(protocolId);
										}
									});
					if (desc != null) {
						return new TargetDescriptorImpl(id, desc, itemNode);
					}
				}				
			}
		} catch (final Exception e) {
		}
		return null;
	}

	public TargetDescriptor getLastSelected() {
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
	public TargetDescriptor findFirst() {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		try {
			final String[] names = preferences.childrenNames();
			if (names.length > 0) {
				return find(names[0]);
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

	public List<TargetDescriptor> findAll() {
		final ConfigurationScope configurationScope = new ConfigurationScope();
		final IEclipsePreferences preferences = configurationScope
				.getNode(QUALIFIER);
		final List<TargetDescriptor> items = new ArrayList<TargetDescriptor>();
		try {
			for (final String name : preferences.childrenNames()) {
				final Preferences itemNode = preferences.node(name);
				final String factoryId = itemNode.get(FACTORY, null);
				if (factoryId != null) {
					final TargetFactoryDescriptor desc = Services
							.run(
									TargetFactories.class,
									new ServiceRunnable<TargetFactories, TargetFactoryDescriptor>() {
										public TargetFactoryDescriptor run(
												TargetFactories service) {
											return service.find(factoryId);
										}
									});
					if (desc != null) {
						TargetDescriptor target = new TargetDescriptorImpl(
								name, desc, itemNode);
						items.add(target);
					}
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
				Preferences itemNode = node.node(item.getId());
				itemNode.put(FACTORY, item.getFactory().getId());
				item.save(itemNode);
			}
			node.flush();
		} catch (final BackingStoreException e) {
		}
		notifyListeners(items);
	}

	private void notifyListeners(final Collection<Target> items) {
		Services.runAll(TargetServiceListener.class,
				new SimpleServiceRunnable<TargetServiceListener>() {
					@Override
					protected void runWithService(TargetServiceListener listener) {
						listener.targetsChanged(items);
					}
				});
	}
}

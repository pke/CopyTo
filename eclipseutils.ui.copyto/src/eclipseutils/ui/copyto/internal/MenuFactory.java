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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.Preferences;

/**
 * Creates the menus for copyto in the main/edit and in popup menus.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class MenuFactory extends ExtensionContributionFactory {
	private final static Bundle bundle = FrameworkUtil
			.getBundle(MenuFactory.class);
	public static final String MENU_URI = "menu:" + bundle.getSymbolicName() //$NON-NLS-1$
			+ ".menu"; //$NON-NLS-1$

	@Override
	public void createContributionItems(final IServiceLocator locator,
			final IContributionRoot root) {
		final IMenuService menuService = (IMenuService) locator
				.getService(IMenuService.class);

		final URL iconEntry = FileLocator.find(bundle, new Path(
				"$nl$/icons/e16/copyto.png"), null); //$NON-NLS-1$
		final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
				.createFromURL(iconEntry) : null;
		// Make sure the preferences are initialized
		final ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(
				new InstanceScope(), "copyto");

		final Preferences node = new InstanceScope().getNode("copyto/targets");
		try {
			final List<Target> targets = new ArrayList<Target>();
			for (final String child : node.childrenNames()) {
				final Target target = new Target(node.node(child));
				targets.add(target);
			}
			Collections.sort(targets, new Comparator<Target>() {
				public int compare(final Target o1, final Target o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for (final Target target : targets) {
				final Map<String, String> parameters = new HashMap<String, String>();
				final CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
						locator, target.getId(), CopyToHandler.COMMAND_ID,
						CommandContributionItem.STYLE_PUSH);
				contributionParameters.label = target.getName();
				contributionParameters.parameters = parameters;
				parameters.put("id", contributionParameters.id);
				parameters.put("url", target.getUrl());
				root.addContributionItem(new CommandContributionItem(
						contributionParameters), null);
			}
		} catch (final Exception e) {
		}
		/*
				final MenuManager menuManager = new MenuManager("Copy To", icon, null) {
					@Override
					public void dispose() {
						menuService.releaseContributions(this);
						super.dispose();
					};
				};
				menuService.populateContributionManager(menuManager, MENU_URI);
				if (menuManager.getSize() == 1) {
					root.addContributionItem(menuManager.getItems()[0], null);
					menuManager.dispose();
				} else {
					root.addContributionItem(menuManager, null);
				}*/
	}
}

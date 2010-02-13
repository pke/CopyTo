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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.ui.copyto.internal.preferences.CopyToPreferencePage;

public class DynamicItem extends CompoundContributionItem implements
		IWorkbenchContribution {
	private final static Bundle bundle = FrameworkUtil
			.getBundle(DynamicItem.class);
	private IServiceLocator locator;
	private static Action action = new Action("Configure...") {
		{
			setId("eclipseutils.ui.copyto.configure");
		}

		@Override
		public void run() {
			CopyToPreferencePage.show(null);
		}
	};

	@Override
	protected IContributionItem[] getContributionItems() {
		return Trackers.run(TargetService.class,
				new ServiceRunnable<TargetService, IContributionItem[]>() {
					public IContributionItem[] run(
							final TargetService targetService) {
						final IContributionItem result[] = new IContributionItem[1];
						final int count = targetService.count();
						List<Target> targets;
						if (0 == count) {
							if ("eclipseutils.ui.copyto.popup".equals(getId())) {
								final MenuManager menuManager = createMenuManager();
								menuManager.add(action);
								result[0] = menuManager;
							} else {
								result[0] = new ActionContributionItem(action);
							}
						} else if (1 == count) {
							final Target first = targetService.findFirst();
							if (first != null) {
								final String format = "Copy To {0}";
								if ("eclipseutils.ui.copyto.dropdown.items"
										.equals(getId())) {
									final MenuManager menuManager = createMenuManager();
									menuManager
											.add(createCommand(format, first));
									menuManager.add(action);
									return new IContributionItem[] {
											createCommand(format, first),
											new Separator(),
											new ActionContributionItem(action) };
								}
							}
						}
						// Still nothing assigned?
						if (null == result[0]) {
							targets = targetService.findAll();
							Collections.sort(targets, new Comparator<Target>() {
								public int compare(final Target o1,
										final Target o2) {
									return o1.getName().compareTo(o2.getName());
								}
							});

							final MenuManager menuManager = createMenuManager();

							for (final Target target : targets) {
								final String format = "{0}";
								menuManager.add(createCommand(format, target));
							}
							menuManager.add(new Separator());
							menuManager.add(action);
							result[0] = menuManager;
						}
						return result;
					}
				});
	}

	private MenuManager createMenuManager() {
		final URL iconEntry = FileLocator.find(bundle, new Path(
				"$nl$/icons/e16/copyto.png"), null); //$NON-NLS-1$
		final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
				.createFromURL(iconEntry) : null;
		final MenuManager menuManager = new MenuManager("Copy To", icon,
				"copyto.menu");
		return menuManager;
	}

	private CommandContributionItem createCommand(final String format,
			final Target target) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		final CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
				locator, target.getId(), CopyToHandler.COMMAND_ID,
				CommandContributionItem.STYLE_PUSH);
		contributionParameters.label = NLS.bind(format, target.getName());
		contributionParameters.parameters = parameters;
		parameters.put("id", target);
		return new CommandContributionItem(contributionParameters);
	}

	public void initialize(final IServiceLocator serviceLocator) {
		this.locator = serviceLocator;
	}

}

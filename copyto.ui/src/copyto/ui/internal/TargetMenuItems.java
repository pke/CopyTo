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
package copyto.ui.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import osgiutils.services.ServiceRunnableFallback;
import osgiutils.services.Services;
import copyto.core.TargetDescriptor;
import copyto.core.TargetService;
import copyto.ui.internal.commands.CopyToHandler;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class TargetMenuItems extends CompoundContributionItem implements
		IWorkbenchContribution {
	private IServiceLocator locator;

	@Override
	protected IContributionItem[] getContributionItems() {
		return Services
				.run(
						TargetService.class,
						new ServiceRunnableFallback<TargetService, IContributionItem[]>() {
							public IContributionItem[] run(
									final TargetService targetService) {
								final int count = targetService.count();
								// We allocate one more item for the separator
								final IContributionItem[] items = new IContributionItem[count > 1 ? count + 1
										: count];
								if (0 == count) {
									return items;
								}
								if (1 == count) {
									final TargetDescriptor first = targetService
											.findFirst();
									if (first != null) {
										final String format = "Copy To {0}";
										items[0] = createCommand(format, first);
									} else {
										return new IContributionItem[0];
									}
								} else {
									// Still nothing assigned?
									final List<TargetDescriptor> targets = targetService
											.findAll();

									Collections.sort(targets,
											new Comparator<TargetDescriptor>() {
												public int compare(
														final TargetDescriptor o1,
														final TargetDescriptor o2) {
													return o1
															.getLabel()
															.compareTo(
																	o2.getLabel());
												}
											});

									int i = 0;
									for (final TargetDescriptor target : targets) {
										final String format = "{0}"; //$NON-NLS-1$
										items[i++] = createCommand(format,
												target);
									}
									items[i] = new Separator();
								}
								return items;
							}

							public IContributionItem[] serviceNotFound() {
								return new IContributionItem[0];
							}
						});
	}

	private CommandContributionItem createCommand(final String format,
			final TargetDescriptor target) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		final CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
				locator, target.getId(), CopyToHandler.COMMAND_ID,
				CommandContributionItem.STYLE_PUSH);
		contributionParameters.label = NLS.bind(format, target.getLabel());
		contributionParameters.parameters = parameters;
		parameters.put("id", target); //$NON-NLS-1$
		return new CommandContributionItem(contributionParameters);
	}

	public void initialize(final IServiceLocator serviceLocator) {
		this.locator = serviceLocator;
	}

}

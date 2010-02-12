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

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.api.Copyable;

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

	private static final Expression visibleExpression = new Expression() {

		<T> T getVariable(final IEvaluationContext context, final String name,
				final Class<T> clazz) {
			final Object object = context.getVariable(name);
			if (object != null
					&& object != IEvaluationContext.UNDEFINED_VARIABLE) {
				if (clazz.isInstance(object)) {
					return clazz.cast(object);
				}
			}

			return null;
		}

		@Override
		public EvaluationResult evaluate(final IEvaluationContext context)
				throws CoreException {
			ISelection selection = getVariable(context,
					ISources.ACTIVE_MENU_SELECTION_NAME, ISelection.class);
			if (selection == null) {
				selection = getVariable(context,
						ISources.ACTIVE_CURRENT_SELECTION_NAME,
						ISelection.class);
			}
			if (selection == null) {
				return EvaluationResult.FALSE;
			}

			if (selection instanceof IStructuredSelection) {
				if (((IStructuredSelection) selection).size() > 1) {
					return EvaluationResult.FALSE;
				}

				final Object element = ((IStructuredSelection) selection)
						.getFirstElement();
				if (!Platform.getAdapterManager().hasAdapter(element,
						Copyable.class.getName())) {
					return EvaluationResult.FALSE;
				}

			} else if (selection instanceof ITextSelection) {
				final IEditorPart part = getVariable(context,
						ISources.ACTIVE_PART_NAME, IEditorPart.class);
				if (null == part
						|| !Platform.getAdapterManager().hasAdapter(part,
								Copyable.class.getName())) {
					return EvaluationResult.FALSE;
				}
			}

			return EvaluationResult.TRUE;
		}
	};

	@Override
	public void createContributionItems(final IServiceLocator locator,
			final IContributionRoot root) {
		final URL iconEntry = FileLocator.find(bundle, new Path(
				"$nl$/icons/e16/copyto.png"), null); //$NON-NLS-1$
		final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
				.createFromURL(iconEntry) : null;

		final List<Target> targets = TargetFactory.load();
		if (targets.isEmpty()) {
			return;
		}

		final MenuManager menuManager = new MenuManager("Copy To", icon, null);

		String format = "{0}";
		if (targets.size() == 1) {
			format = "CopyTo {0}";
		} else {
			Collections.sort(targets, new Comparator<Target>() {
				public int compare(final Target o1, final Target o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		for (final Target target : targets) {
			final Map<String, String> parameters = new HashMap<String, String>();
			final CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
					locator, target.getId(), CopyToHandler.COMMAND_ID,
					CommandContributionItem.STYLE_PUSH);
			contributionParameters.label = NLS.bind(format, target.getName());
			contributionParameters.parameters = parameters;
			parameters.put("id", contributionParameters.id);
			parameters.put("url", target.getUrl());
			menuManager
					.add(new CommandContributionItem(contributionParameters));
		}
		if (menuManager.getSize() == 1) {
			root.addContributionItem(menuManager.getItems()[0],
					visibleExpression);
			menuManager.dispose();
		} else {
			root.addContributionItem(menuManager, visibleExpression);
		}
	}
}

package eclipseutils.ui.copyto.internal;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class DynamicItem extends CompoundContributionItem implements
		IWorkbenchContribution {
	private final static Bundle bundle = FrameworkUtil
			.getBundle(MenuFactory.class);
	private IServiceLocator locator;

	@Override
	protected IContributionItem[] getContributionItems() {
		final List<Target> targets = TargetFactory.load();
		if (targets.isEmpty()) {
			return new IContributionItem[0];
		}

		IContributionItem result;
		if (targets.size() == 1) {
			final String format = "Copy To {0}";
			result = createCommand(format, targets.get(0));
		} else {
			Collections.sort(targets, new Comparator<Target>() {
				public int compare(final Target o1, final Target o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});

			final URL iconEntry = FileLocator.find(bundle, new Path(
					"$nl$/icons/e16/copyto.png"), null); //$NON-NLS-1$
			final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
					.createFromURL(iconEntry) : null;
			final MenuManager menuManager = new MenuManager("Copy To", icon,
					"copyto.menu");

			for (final Target target : targets) {
				final String format = "{0}";

				menuManager.add(createCommand(format, target));
			}
			result = menuManager;
		}

		return new IContributionItem[] { result };
	}

	private CommandContributionItem createCommand(final String format,
			final Target target) {
		final Map<String, String> parameters = new HashMap<String, String>();
		final CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(
				locator, target.getId(), CopyToHandler.COMMAND_ID,
				CommandContributionItem.STYLE_PUSH);
		contributionParameters.label = NLS.bind(format, target.getName());
		contributionParameters.parameters = parameters;
		parameters.put("id", contributionParameters.id);
		parameters.put("url", target.getUrl());
		return new CommandContributionItem(contributionParameters);
	}

	public void initialize(final IServiceLocator serviceLocator) {
		this.locator = serviceLocator;
	}

}

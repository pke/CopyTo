package eclipseutils.ui.copyto.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class TargetFactory {

	private static final String QUALIFIER = FrameworkUtil.getBundle(
			Target.class).getSymbolicName()
			+ "/targets";

	public static List<Target> load() {
		final IEclipsePreferences preferences = new ConfigurationScope()
				.getNode(QUALIFIER);
		final List<Target> items = new ArrayList<Target>();
		try {
			for (final String name : preferences.childrenNames()) {
				final Target item = new Target(preferences.node(name));
				if (item != null) {
					items.add(item);
				}
			}
		} catch (final BackingStoreException e) {
		}
		return items;
	}

	public static void save(final Collection<Target> items) {
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

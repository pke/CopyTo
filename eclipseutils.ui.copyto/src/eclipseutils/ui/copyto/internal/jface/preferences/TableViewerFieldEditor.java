package eclipseutils.ui.copyto.internal.jface.preferences;

import java.util.Iterator;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Implementation of a table viewer editor that saves items in a hierarchical
 * tree.
 * 
 * <p>
 * It create nodes with the items ID (returned by {@link #getId(Object)} under
 * the <code>preferencePath</code>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            type of the items in the table
 */
public abstract class TableViewerFieldEditor<T> extends
		AbstractTableViewerFieldEditor<T> {

	protected TableViewerFieldEditor(final String preferencePath,
			final String labelText, final Composite parent, final int flags) {
		super(preferencePath, labelText, parent, flags);
	}

	/**
	 * @param item
	 *            to get the id from. Must <b>not</b> be <code>null</code>.
	 * @return the id for the item.
	 */
	protected abstract String getId(T item);

	/**
	 * Creates a new item.
	 * 
	 * <p>
	 * Implementors should not call {@link #add(T)} themselves. It's called by
	 * the caller of this method.
	 * 
	 * @param preferences
	 *            to create the item from
	 * @return the newly created item or <code>null</code> if no item could be
	 *         constructed from the given <i>preferences</i>.
	 * 
	 */
	protected abstract T loadItem(Preferences preferences);

	protected IScopeContext getScopeContext() {
		return new InstanceScope();
	}

	@Override
	protected void doStore() {
		final IScopeContext instanceScope = getScopeContext();
		Preferences node = instanceScope.getNode(getPreferenceName());
		try {
			node.removeNode();
			if (!presentsDefaultValue()) {
				node = instanceScope.getNode(getPreferenceName());
				final Iterator<T> it = getItems().iterator();
				while (it.hasNext()) {
					final T item = it.next();
					store(item, node.node(getId(item)));
				}
				node.flush();
			}
		} catch (final BackingStoreException e) {
		}
	}

	protected abstract void store(T item, Preferences node);

	@Override
	protected void doLoad() {
		final Preferences node = getScopeContext().getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (final BackingStoreException e) {
		}
	}

	@Override
	protected void doLoadDefault() {
		final Preferences node = new DefaultScope()
				.getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (final BackingStoreException e) {
		}
	}

	/**
	 * The default implementation calls {@link #loadItem(Preferences)} with each
	 * child node of the given <i>instanceNode</i>.
	 * 
	 * Sub-classes may overwrite to provide a different behavior.
	 * 
	 * @param instanceNode
	 * @param childrenNames
	 */
	protected void doLoad(final Preferences instanceNode,
			final String... childrenNames) {
		for (final String key : childrenNames) {
			final T item = loadItem(instanceNode.node(key));
			if (item != null) {
				add(item);
			}
		}
	}

	/**
	 * The default implementation just calls
	 * {@link #doLoad(Preferences, String...)}
	 * 
	 * @param defaultNode
	 * @param childrenNames
	 */
	protected void doLoadDefault(final Preferences defaultNode,
			final String... childrenNames) {
		doLoad();
	}

}

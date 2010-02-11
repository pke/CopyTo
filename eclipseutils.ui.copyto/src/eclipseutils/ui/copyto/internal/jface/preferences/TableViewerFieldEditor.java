package eclipseutils.ui.copyto.internal.jface.preferences;

import java.util.Iterator;

import org.eclipse.core.runtime.preferences.DefaultScope;
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

	protected TableViewerFieldEditor(String preferencePath, String labelText,
			Composite parent, int flags) {
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

	@Override
	protected void doStore() {
		InstanceScope instanceScope = new InstanceScope();
		Preferences node = instanceScope.getNode(getPreferenceName());
		try {
			node.removeNode();
			if (!presentsDefaultValue()) {
				node = instanceScope.getNode(getPreferenceName());
				Iterator<T> it = getItems().iterator();
				while (it.hasNext()) {
					T item = it.next();
					store(item, node.node(getId(item)));
				}
				node.flush();
			}
		} catch (BackingStoreException e) {
		}
	}

	@Override
	protected void doLoad() {
		final Preferences node = new InstanceScope()
				.getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (BackingStoreException e) {
		}
	}

	@Override
	protected void doLoadDefault() {
		Preferences node = new DefaultScope().getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (BackingStoreException e) {
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
	protected void doLoad(Preferences instanceNode, String... childrenNames) {
		for (String key : childrenNames) {
			T item = loadItem(instanceNode.node(key));
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
	protected void doLoadDefault(Preferences defaultNode,
			String... childrenNames) {
		doLoad();
	}

}

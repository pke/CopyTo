package eclipseutils.core.extensions.internal;

public abstract class VoidVisitor<T> implements Visitor<T, Object> {

	public final Object visit(T item) {
		if (!accept(item)) {
			return false;
		}
		return null;
	}

	/**
	 * @param item
	 * @return <code>true</code> to continue to visit other items.
	 */
	protected abstract boolean accept(T item);
}
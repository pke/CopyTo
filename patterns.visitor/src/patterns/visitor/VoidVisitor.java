package patterns.visitor;

/**
 * A visitor that can be used if no return values has to be returned.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 * @param <T>
 */
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
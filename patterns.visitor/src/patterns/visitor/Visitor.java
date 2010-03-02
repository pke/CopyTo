package patterns.visitor;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            visited item type
 * @param <R>
 *            return value type
 * @see Visitors
 */
public interface Visitor<T, R> {
	/**
	 * Called when a service has been found.
	 * 
	 * @param service
	 *            that was found. Never <code>null</code>.
	 * @return an implementation defined value.
	 */
	R visit(T item);
}
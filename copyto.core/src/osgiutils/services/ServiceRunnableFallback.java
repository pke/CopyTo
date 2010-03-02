package osgiutils.services;

/**
 * Extends ServiceRunnable with a fallback for the case when the requested
 * service was not found.
 * 
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            service type
 * @param <R>
 *            return value type
 */
public interface ServiceRunnableFallback<T, R> extends ServiceRunnable<T, R> {
	/**
	 * Called when no service was found.
	 * 
	 * @return an implementation defined value.
	 */
	R serviceNotFound();
}

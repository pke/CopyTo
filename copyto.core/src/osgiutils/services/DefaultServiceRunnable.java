package osgiutils.services;

/**
 * Handles the return of a default value, if the service is not available.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            Service class
 * @param <R>
 *            return value type
 */
public abstract class DefaultServiceRunnable<T, R> implements
		ServiceRunnableFallback<T, R> {

	private final R defaultReturn;

	/**
	 * Creates the runnable with the given default return value.
	 * 
	 * <p>
	 * Please not that for collections as default return value you cannot
	 * specify Collections.emptyList() but must specify Collections.EMPTY_LIST
	 * instead.
	 * 
	 * @param defaultReturn
	 */
	public DefaultServiceRunnable(final R defaultReturn) {
		this.defaultReturn = defaultReturn;
	}

	public R serviceNotFound() {
		return defaultReturn;
	}
}

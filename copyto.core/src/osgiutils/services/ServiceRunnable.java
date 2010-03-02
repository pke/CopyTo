package osgiutils.services;


/**
 * A {@link Runnable}-like interface for code that should be executed with a
 * specific OSGi service.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            service type
 * @param <R>
 *            return value type
 * @see Services
 */
public interface ServiceRunnable<T, R> {
	/**
	 * Called when a service has been found.
	 * 
	 * @param service
	 *            that was found. Never <code>null</code>.
	 * @return an implementation defined value.
	 */
	R run(T service);
}

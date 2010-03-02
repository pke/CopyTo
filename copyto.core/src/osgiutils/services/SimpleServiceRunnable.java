package osgiutils.services;

/**
 * Abstract implementation of ServiceRunnableFallback for "void" behaviour.
 * 
 * <p>
 * Use this class if you do not need to return values from the runnable.
 * 
 * <h2>Example</h2>
 * 
 * <pre>
 * Trackers.run(LogService.class, new SimpleServiceRunnable&lt;LogService&gt;() {
 * 
 * 	protected void doRun(final LogService service) {
 * 		service.log(level, text, t);
 * 	}
 * 
 * 	protected void doRun() {
 * 		if (level == LogService.LOG_ERROR) {
 * 			if (t != null) {
 * 				t.printStackTrace(System.err);
 * 			} else {
 * 				System.err.println(text);
 * 			}
 * 		} else {
 * 			System.out.println(text);
 * 		}
 * 	}
 * });
 * </pre>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            service type
 */
public abstract class SimpleServiceRunnable<T> implements
		ServiceRunnableFallback<T, Object> {

	public final Object run(final T service) {
		runWithService(service);
		return null;
	}

	public final Object serviceNotFound() {
		runWithoutService();
		return null;
	}

	/**
	 * Subclasses can override to implement functionality for that case that the
	 * service was not found.
	 * 
	 * The default implementation does nothing.
	 */
	protected void runWithoutService() {
	}

	/**
	 * Called when a service was found.
	 * 
	 * @param service
	 *            that was found. Never <code>null</code>.
	 */
	protected abstract void runWithService(T service);
}

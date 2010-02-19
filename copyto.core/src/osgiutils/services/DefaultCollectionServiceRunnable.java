package osgiutils.services;

import java.util.Collection;
import java.util.Collections;

/**
 * Specialized implementation of a {@link DefaultServiceRunnable} that sets an
 * empty (List-)collection as the default return value.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            service type
 * @param <R>
 *            return value type
 */
public abstract class DefaultCollectionServiceRunnable<T, R> extends
		DefaultServiceRunnable<T, Collection<R>> {

	/**
	 * Creates the runnable with an empty list as default return value.
	 */
	@SuppressWarnings("unchecked")
	public DefaultCollectionServiceRunnable() {
		super(Collections.EMPTY_LIST);
	}

}

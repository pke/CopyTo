/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/

package osgiutils.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Easier calling of service methods.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class Services {

	private static BundleContext context = FrameworkUtil.getBundle(
			Services.class).getBundleContext();

	private Services() {
	}

	/**
	 * Runs the a runnable with the requested service.
	 * 
	 * 
	 * @param <T>
	 *            Service type
	 * @param <R>
	 *            return value type
	 * @param serviceClass
	 * @param runnable
	 * @return what the <i>runnable</i> returns, if the requested service has
	 *         been found. If the runnable also implements
	 *         {@link ServiceRunnableFallback} then the value of its
	 *         {@link ServiceRunnableFallback#run()} is returned. Otherwise
	 *         <code>null</code> is returned.
	 * @see SimpleServiceRunnable
	 */
	public static <T, R> R run(final Class<T> serviceClass,
			final ServiceRunnable<T, R> runnable) {
		return runService(context.getServiceReference(serviceClass.getName()),
				runnable);
	}

	/**
	 * @param <T>
	 * @param <R>
	 * @param serviceClass
	 * @param runnable
	 * @return a collection of items of the return type.
	 */
	public static <T, R> Collection<R> runAll(final Class<T> serviceClass,
			final ServiceRunnable<T, R> runnable) {
		return runAll(serviceClass, null, runnable);
	}

	/**
	 * @param <T>
	 * @param <R>
	 * @param serviceClass
	 * @param filter
	 * @param runnable
	 * @return a collection of items of the return type.
	 */
	public static <T, R> Collection<R> runAll(final Class<T> serviceClass,
			final String filter, final ServiceRunnable<T, R> runnable) {
		try {
			final ServiceReference[] references = context.getServiceReferences(
					serviceClass.getName(), filter);
			if (references != null) {

				final Collection<R> results = new ArrayList<R>(
						references.length);
				for (final ServiceReference reference : references) {
					results.add(runService(reference, runnable));
				}

				return results;
			}
		} catch (final InvalidSyntaxException e) {
		}
		return Collections.emptyList();
	}

	private static <T, R> R run(final T service,
			final ServiceRunnable<T, R> runnable) {
		if (service != null) {
			return runnable.run(service);
		} else if (runnable instanceof ServiceRunnableFallback<?, ?>) {
			return ((ServiceRunnableFallback<T, R>) runnable).serviceNotFound();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T, R> R runService(final ServiceReference reference,
			final ServiceRunnable<T, R> runnable) {
		if (reference != null) {
			try {
				final T service = (T) context.getService(reference);
				return run(service, runnable);
			} finally {
				context.ungetService(reference);
			}
		} else if (runnable instanceof ServiceRunnableFallback<?, ?>) {
			return ((ServiceRunnableFallback<T, R>) runnable).serviceNotFound();
		}
		return null;
	}
}

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

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Automatic management of OSGi service trackers.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class Trackers {

	private static Map<Class<?>, ServiceTracker> trackers = new HashMap<Class<?>, ServiceTracker>();

	/**
	 * Runs the a runnable with the requested service.
	 * 
	 * <p>
	 * This method automatically creates a ServiceTracker for the requested
	 * service type.
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
		final T service = getService(serviceClass);
		return runService(runnable, service);
	}

	/**
	 * @param <T>
	 * @param serviceClass
	 * @param runnable
	 */
	public static <T> void runAll(final Class<T> serviceClass,
			final ServiceRunnable<T, ?> runnable) {
		final T services[] = getServices(serviceClass);
		for (final T service : services) {
			runService(runnable, service);
		}
	}

	private static <T, R> R runService(final ServiceRunnable<T, R> runnable,
			final T service) {
		if (service != null) {
			return runnable.run(service);
		} else if (runnable instanceof ServiceRunnableFallback<?, ?>) {
			return ((ServiceRunnableFallback<T, R>) runnable).run();
		}
		return null;
	}

	private static <T> T getService(final Class<T> serviceClass) {
		final ServiceTracker tracker = getTracker(serviceClass);
		return serviceClass.cast(tracker.getService());
	}

	@SuppressWarnings("unchecked")
	private static <T> T[] getServices(final Class<T> serviceClass) {
		final ServiceTracker tracker = getTracker(serviceClass);
		final Object[] services = tracker.getServices();
		return (T[]) (services != null ? (T[]) tracker.getServices()
				: new Object[0]);
	}

	private static <T> ServiceTracker getTracker(final Class<T> serviceClass) {
		ServiceTracker tracker = trackers.get(serviceClass);
		if (null == tracker) {
			tracker = createTracker(serviceClass);
			trackers.put(serviceClass, tracker);
		}
		return tracker;
	}

	private static <T> ServiceTracker createTracker(final Class<T> serviceClass) {
		return new ServiceTracker(FrameworkUtil.getBundle(Trackers.class)
				.getBundleContext(), serviceClass.getName(), null) {
			{
				open();
			}
		};
	}

	private Trackers() {
	}
}

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

package eclipseutils.core.extensions.internal;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Easier calling of service methods.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class Visitors {

	private Visitors() {
	}

	/**
	 * Visits a set of items.
	 * 
	 * @param <T>
	 *            item type
	 * @param <R>
	 *            return value type
	 * @param items
	 * @param visitor
	 * @return what the <i>visitor</i> returns, if the requested service has
	 *         been found. If the runnable also implements
	 *         {@link ServiceRunnableFallback} then the value of its
	 *         {@link ServiceRunnableFallback#run()} is returned. Otherwise
	 *         <code>null</code> is returned.
	 * @see SimpleServiceRunnable
	 */
	public static <T, R> R visit(T[] items, Visitor<T, R> visitor) {
		return visit(items, null, visitor);
	}

	/**
	 * Visits all items.
	 * 
	 * <p>
	 * The result returned by the visitor must implement equals and hashCode to
	 * be correctly inserted into the returned Set.
	 * 
	 * @param <T>
	 *            type of items to visit
	 * @param <R>
	 *            type of returned items
	 * @param items
	 * @param visitor
	 * @return
	 */
	public static <T, R> Set<R> visitAllUnique(T[] items, Visitor<T, R> visitor) {
		Set<R> results = new HashSet<R>();
		return visitAll(items, results, visitor);
	}

	public static <T, R, C extends Collection<R>> C visitAll(T[] items,
			C results, Visitor<T, R> visitor) {
		for (T item : items) {
			R result = visit(item, visitor);
			if (result != null) {
				results.add(result);
			}
		}
		return results;
	}

	public static <T, R> R visit(T[] items, R defaultResult,
			Visitor<T, R> visitor) {
		for (T item : items) {
			R result = visit(item, visitor);
			if (result != null) {
				return result;
			}
		}
		return defaultResult;
	}

	private static <T, R> R visit(T item, Visitor<T, R> visitor) {
		try {
			return visitor.visit(item);
		} catch (Throwable t) {
		}
		return null;
	}
}

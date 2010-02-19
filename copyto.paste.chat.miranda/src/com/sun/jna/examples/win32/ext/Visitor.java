package com.sun.jna.examples.win32.ext;

/**
 * A generic typed visitor.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 * @param <T> type of the items to visit.
 */
public interface Visitor<T> {
	boolean visit(T item);
}
package com.sun.jna.examples.win32.ext;

import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.User32.WNDENUMPROC;
import com.sun.jna.examples.win32.W32API.HWND;

/**
 * Windows related methods.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Windows {

	/**
	 * Iterates over all top-level windows and calls the given visitor for each
	 * found window.
	 * 
	 * @param visitor
	 */
	public static void visitWindows(final Visitor<HWND> visitor) {
		User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer data) {
				return visitor.visit(hWnd);
			}
		}, null);
	}
}
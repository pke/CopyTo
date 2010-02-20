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
package com.sun.jna.examples.win32.ext;

import com.sun.jna.Native;
import com.sun.jna.examples.win32.W32API;

/**
 * Incomplete Shell32.dll implementation.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Shell32 extends W32API {
	Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class,
			DEFAULT_OPTIONS);

	int ExtractIcon(HINSTANCE hInst, String lpszExeFileName, int nIconIndex);
	int ExtractIcon(int hInst, String lpszExeFileName, int nIconIndex);
}

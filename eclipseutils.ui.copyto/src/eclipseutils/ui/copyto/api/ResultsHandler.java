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
package eclipseutils.ui.copyto.api;

import org.eclipse.jface.window.IShellProvider;

/**
 * Performs some actions on a set of Results.
 * 
 * @author <a href="mailto:kursawe@topsystem.de">Philipp Kursawe</a>
 * 
 */
public interface ResultsHandler {
	/**
	 * 
	 * @param results
	 * @param shellProvider
	 * @uithread This is called in the UI-thread.
	 */
	void handleResults(Results results, IShellProvider shellProvider);
}

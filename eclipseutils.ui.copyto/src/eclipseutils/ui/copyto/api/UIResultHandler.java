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

import org.eclipse.swt.widgets.Shell;

/**
 * Results handler that wants to display something in the UI.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface UIResultHandler {
	/**
	 * 
	 * @param result
	 * @param shellProvider
	 * @uithread This method is called from the UI-Thread.
	 */
	void handleResults(Results result, Shell shell);
}

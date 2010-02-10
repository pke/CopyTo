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
package eclipseutils.ui.copyto.internal;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Result;

/**
 * Handles the copying of a specific text.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * @since 1.0
 */
public interface Handler {
	/**
	 * @param text
	 *            contained in the item
	 * @param item
	 *            the text was generated from
	 * @param monitor
	 *            to report progress on the copying of the text
	 * @return
	 * @throws Exception
	 */
	Result copy(final Copyable copyable, Map<String, String> params,
			IProgressMonitor monitor);

	String getId();
}
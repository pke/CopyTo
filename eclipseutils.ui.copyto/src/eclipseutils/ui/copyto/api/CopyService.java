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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.IShellProvider;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface CopyService {
	/**
	 * This will try to copy all the <i>copyable</i> to a target.
	 * 
	 * <p>
	 * It will inform ResultHandler about the results of the copy process.
	 * 
	 * @param targetId
	 * @param monitor
	 *            TODO
	 * @param shellProvider
	 * @param copyables
	 * @param copyable
	 * @return the results of the copy action
	 */
	Results copy(String targetId, IProgressMonitor monitor,
			IShellProvider shellProvider, Copyable... copyables);
}

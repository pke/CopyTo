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
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Protocol {
	/**
	 * @param copyable
	 *            to copy
	 * @param shell
	 *            a shell to use as parent for UI
	 * @param monitor
	 *            to report progress
	 * @return the results of the copy action.
	 * @throws Exception
	 *             if an error occurred during copying.
	 */
	String copy(Copyable copyable, Shell shell, final IProgressMonitor monitor)
			throws Exception;
}

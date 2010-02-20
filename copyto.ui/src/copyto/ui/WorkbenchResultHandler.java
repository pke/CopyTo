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
package copyto.ui;

import org.eclipse.ui.IWorkbench;

/**
 * Used for actions that need a workbench to run.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public interface WorkbenchResultHandler extends UIResultHandler {
	 /**
     * Initializes this results handler for the given workbench.
     * <p>
     * This method is called automatically as the handler is being created
     * and initialized. Clients must not call this method.
     * </p>
     *
     * @param workbench the workbench
     */
	void init(IWorkbench workbench);
}

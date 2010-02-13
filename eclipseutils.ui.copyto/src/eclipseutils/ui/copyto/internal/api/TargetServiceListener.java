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

package eclipseutils.ui.copyto.internal.api;

import java.util.Collection;


/**
 * Notified by the TargetService about changes in targets.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface TargetServiceListener {
	/**
	 * @param changedTargets
	 */
	void targetsChanged(Collection<Target> changedTargets);
}

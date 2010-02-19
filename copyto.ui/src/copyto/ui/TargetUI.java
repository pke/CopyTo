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

import org.eclipse.swt.widgets.Shell;

import copyto.core.Copyable;
import copyto.core.Target;


/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface TargetUI {

	/**
	 * Shows a UI for the given copyable.
	 * 
	 * @param parent
	 * @param copyable
	 * @param target
	 * @return a probably modified Copyable or the original handed in. Must
	 *         <b>not</b> be <code>null</code>.
	 */
	Copyable show(Shell parent, Copyable copyable, Target target);
}

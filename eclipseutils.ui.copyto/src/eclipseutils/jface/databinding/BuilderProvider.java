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
package eclipseutils.jface.databinding;

import org.eclipse.swt.widgets.Composite;

/**
 * Provides access to a <code>Builder</code>.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * @see BuiltTitleAreaDialog
 */
public interface BuilderProvider {
	/**
	 * @param parent
	 * @return a builder, never <code>null</code>.
	 */
	Builder createBuilder(Composite parent);
}

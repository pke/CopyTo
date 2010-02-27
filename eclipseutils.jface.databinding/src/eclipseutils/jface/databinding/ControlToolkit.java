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

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Unified interface for control creation that mirros the FormToolkit methods
 * for creating widgets.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface ControlToolkit {
	/**
	 * @param parent
	 * @param text
	 * @param style
	 * @return a button control.
	 */
	Button createButton(Composite parent, String text, int style);

	/**
	 * @param parent
	 * @param text
	 * @param style
	 * @return a text control.
	 */
	Text createText(final Composite parent, int style);

	/**
	 * @param parent
	 * @param text
	 * @param style
	 * @return a label control.
	 */
	Label createLabel(Composite parent, String text, int style);

	Combo createCombo(Composite parent, int style);
}

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
package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FormControlCreator implements ControlCreator {

	private final FormToolkit toolkit;

	public FormControlCreator(final FormToolkit toolkit) {
		this.toolkit = toolkit;
	}

	public Button createButton(final Composite parent, final String text,
			final int style) {
		return toolkit.createButton(parent, text, style);
	}

	public Text createText(final Composite parent, final String text,
			final int style) {
		return toolkit.createText(parent, text, style);
	}

	public Label createLabel(final Composite parent, final String text,
			final int style) {
		return toolkit.createLabel(parent, text, style);
	}
}

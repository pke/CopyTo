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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

class SWTControlCreator implements ControlCreator {
	private static SWTControlCreator instance;

	public static ControlCreator getInstance() {
		if (instance == null) {
			instance = new SWTControlCreator();
		}
		return instance;
	}

	public Button createButton(final Composite parent, final String text,
			final int style) {
		final Button control = new Button(parent, style);
		return setText(control, text);
	}

	public Text createText(final Composite parent, final String text,
			final int style) {
		final Text control = new Text(parent, style);
		return setText(control, text);
	}

	public Label createLabel(final Composite parent, final String text,
			final int style) {
		final Label control = new Label(parent, style);
		return setText(control, text);
	}

	protected static <T extends Control> T setText(final T control,
			final String text) {
		if (text != null) {
			if (control instanceof Button) {
				((Button) control).setText(text);
			} else if (control instanceof Label) {
				((Label) control).setText(text);
			} else if (control instanceof Text) {
				((Text) control).setText(text);
			}
		}
		return control;
	}
}
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

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



/**
 * Creates a text editor.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class TextEditorCreator extends AbstractEditorCreator {

	public IObservableValue create(final ControlCreator toolkit,
			final Composite parent, final Object bean, final String property) {
		final Text text = toolkit.createText(parent, null, SWT.BORDER);
		return SWTObservables.observeText(text, SWT.Modify);
	}

	@Override
	public boolean hasLabel() {
		return true;
	}
}
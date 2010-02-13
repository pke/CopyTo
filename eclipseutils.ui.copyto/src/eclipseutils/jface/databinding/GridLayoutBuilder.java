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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;



/**
 * Builds a Bean Editing/Viewing UI using the GridLayout with 2 columns.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class GridLayoutBuilder extends AbstractBuilder {

	public GridLayoutBuilder(final ControlCreator toolkit,
			final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		super(toolkit, parent, bean, targetToModelPolicy);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
	}

	public GridLayoutBuilder(final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		this(SWTControlCreator.getInstance(), parent, bean, targetToModelPolicy);
	}

	@Override
	protected void applyLayout(final Label label, final Control control) {
		if (label != null) {
			GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(
					label);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
		} else {
			GridDataFactory.fillDefaults().grab(true, false).span(2, 1)
					.applyTo(control);
		}
	}
}

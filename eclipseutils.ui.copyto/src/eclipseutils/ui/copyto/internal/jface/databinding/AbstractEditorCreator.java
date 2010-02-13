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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractEditorCreator implements EditorCreator {
	private static final String LABEL_TOOLTIP = "label.tooltip";

	/**
	 * @param control
	 * @param bean
	 * @param property
	 */
	public static void setToolTip(final Control control, final Object bean,
			final String property) {
		final String desc = LocalizationHelper.getDescription(bean, property);
		if (desc != null) {
			control.setToolTipText(desc);
			Color color = JFaceResources.getColorRegistry().get(LABEL_TOOLTIP);
			if (null == color) {
				JFaceResources.getColorRegistry().put(
						LABEL_TOOLTIP,
						control.getDisplay().getSystemColor(
								SWT.COLOR_LIST_SELECTION).getRGB());
				color = JFaceResources.getColorRegistry().get(LABEL_TOOLTIP);
			}
			control.setForeground(color);
		}
	}

	public boolean hasLabel() {
		return false;
	}
}

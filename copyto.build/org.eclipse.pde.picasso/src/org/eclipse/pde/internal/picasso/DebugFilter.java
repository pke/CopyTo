/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.picasso;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class DebugFilter implements Listener {
	private int extraCompositeMargin;
	private boolean toolTip;
	
	public DebugFilter(int extraCompositeMargin, boolean toolTip) {
		super();
		this.extraCompositeMargin = extraCompositeMargin;
		this.toolTip = toolTip;
	}
	
	public void handleEvent(Event event) {
		Widget widget = event.widget;
		if (widget instanceof Control) {
			Painter.decorate((Control) widget, extraCompositeMargin, toolTip);
		}
	}
}

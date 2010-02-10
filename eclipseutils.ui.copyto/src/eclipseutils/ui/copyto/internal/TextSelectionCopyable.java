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
package eclipseutils.ui.copyto.internal;

import org.eclipse.jface.text.ITextSelection;

import eclipseutils.ui.copyto.api.Copyable;

class TextSelectionCopyable implements Copyable {
	private final ITextSelection selection;

	TextSelectionCopyable(final ITextSelection selection) {
		this.selection = selection;
	}

	public String getText() {
		return this.selection.getText();
	}

	public String getMimeType() {
		return "plain/text"; //$NON-NLS-1$
	}

	public Object getSource() {
		return this.selection;
	}
}
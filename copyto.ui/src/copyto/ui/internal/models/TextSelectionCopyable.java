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
package copyto.ui.internal.models;

import org.eclipse.jface.text.ITextSelection;

import copyto.core.Copyable;


public class TextSelectionCopyable implements Copyable {
	private final ITextSelection selection;

	public TextSelectionCopyable(final ITextSelection selection) {
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
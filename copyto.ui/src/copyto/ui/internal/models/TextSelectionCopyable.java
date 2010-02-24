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
	
	private String trimRight(String string) {
		int length = string.length();
		int len = length - 1;
		
		char c;
		while ((len>0) && ((c = string.charAt(len)) <= ' ')) {
		    len--;
		}
		return (len < length) ? string.substring(0, len+1) : string;
	}

	public String getText() {
		return trimRight(this.selection.getText());
	}

	public String getMimeType() {
		return "plain/text"; //$NON-NLS-1$
	}

	public Object getSource() {
		return this.selection;
	}
}
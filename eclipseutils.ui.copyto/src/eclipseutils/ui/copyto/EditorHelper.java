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
package eclipseutils.ui.copyto;

import java.lang.reflect.Method;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.AbstractTextEditor;

/**
 * Helpers for common work done on editors.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class EditorHelper {

	/**
	 * Calls AbstractTextEditor.getSourceViewer() through reflection, as that
	 * method is normally protected (for some ungodly reason).
	 * 
	 * @param editor
	 *            to run reflection on
	 * @return the ITextViewer interface for the source viewer or
	 *         <code>null</code> if <i>editor</i> has no "getSourceViewer"
	 *         method.
	 */
	public static ITextViewer getSourceViewer(final AbstractTextEditor editor) {
		try {
			final Method method = AbstractTextEditor.class
					.getDeclaredMethod("getSourceViewer"); //$NON-NLS-1$
			method.setAccessible(true);
			return (ITextViewer) method.invoke(editor);
		} catch (final Exception e) {
			return null;
		}
	}

	private EditorHelper() {
	}
}

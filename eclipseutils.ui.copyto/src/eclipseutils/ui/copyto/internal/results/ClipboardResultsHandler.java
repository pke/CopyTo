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
package eclipseutils.ui.copyto.internal.results;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.UIResultHandler;
import eclipseutils.ui.copyto.internal.Messages;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ClipboardResultsHandler implements UIResultHandler {

	/**
	 * 
	 */
	public static final String CLIPBOARD_ALWAYS_OVERWRITE = "clipboard.alwaysOverwrite"; //$NON-NLS-1$

	/**
	 * @param results
	 * @param shellProvider
	 */
	public void handleResults(final Results results,
			final IShellProvider shellProvider) {
		final IPreferenceStore prefs = new ScopedPreferenceStore(
				new ConfigurationScope(), FrameworkUtil.getBundle(getClass())
						.getSymbolicName());
		final boolean overwrite = prefs.getBoolean(CLIPBOARD_ALWAYS_OVERWRITE);

		final String joinedURLs = joinURLs(results.getSuccesses());
		if (joinedURLs.length() > 0) {
			final Clipboard clipboard = new Clipboard(Display.getDefault());
			try {
				if (!overwrite) {
					final TransferData[] availableTypes = clipboard
							.getAvailableTypes();
					if (availableTypes.length > 0) {
						final MessageDialogWithToggle dialog = MessageDialogWithToggle
								.openYesNoQuestion(
										shellProvider.getShell(),
										Messages.ClipboardResultsHandler_Title,
										NLS.bind(
												Messages.ClipboardResultsHandler_Message,
												results.getTarget().getName(),
												joinedURLs),
										Messages.ClipboardResultsHandler_Toggle,
										false, null, null);
						if (IDialogConstants.YES_ID != dialog.getReturnCode()) {
							return;
						}
						if (dialog.getToggleState()) {
							prefs.setValue(CLIPBOARD_ALWAYS_OVERWRITE, true);
						}
					}
				}

				clipboard.setContents(new String[] { joinedURLs },
						new Transfer[] { TextTransfer.getInstance() });
			} catch (final SWTError e) {
				if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD) {
					throw e;
				}
				/*
				 * if (MessageDialog.openQuestion(getShell(),
				 * ActionMessages.CopyQualifiedNameAction_ErrorTitle,
				 * ActionMessages.CopyQualifiedNameAction_ErrorDescription)) {
				 * clipboard.setContents(data, dataTypes); }
				 */
			} finally {
				clipboard.dispose();
			}
		}
	}

	private String joinURLs(final Collection<Result> results) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<Result> it = results.iterator();
		while (it.hasNext()) {
			final Result result = it.next();
			if (result.getStatus().isOK()) {
				final URL url = result.getLocation();
				if (url != null) {
					sb.append(url.toString());
					if (it.hasNext()) {
						sb.append(","); //$NON-NLS-1$
					}
				}
			}
		}
		return sb.toString();
	}
}

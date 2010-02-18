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
package eclipseutils.ui.copyto.win32.miranda.internal;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;

import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.UIResultHandler;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ChatRoomPaste implements UIResultHandler {

	final int IDC_MESSAGE = 1009;
	final int IDC_LIST = 1072;

	public void handleResults(Results result, IShellProvider shellProvider) {
		MirandaIRC mirandaIRC = MirandaIRC.find();
		if (mirandaIRC != null) {
			FilteredParticipantsSelectionDialog dialog = new FilteredParticipantsSelectionDialog(
					shellProvider.getShell(), mirandaIRC);
			if (Window.OK == dialog.open()) {
				Participant participant = (Participant) dialog.getFirstResult();
				participant.sendMessage(joinURLs(result.getSuccesses()));
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

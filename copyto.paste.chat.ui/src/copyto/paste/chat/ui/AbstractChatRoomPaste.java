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
package copyto.paste.chat.ui;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.osgi.framework.Bundle;

import copyto.core.Result;
import copyto.core.Results;
import copyto.paste.chat.core.ChannelUser;
import copyto.paste.chat.core.ChatRoom;
import copyto.paste.chat.core.ChatUser;
import copyto.ui.UIResultHandler;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class AbstractChatRoomPaste implements UIResultHandler {

	protected abstract boolean canHandleResults(Results results);

	protected abstract Collection<ChatRoom> getChatRooms();

	public void handleResults(Results results, Shell shell) {
		if (!canHandleResults(results)) {
			return;
		}

		SelectionStatusDialog dialog = new FilteredParticipantsSelectionDialog(
				shell) {

			@Override
			protected void fillContentProvider(
					AbstractContentProvider contentProvider,
					ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
					throws CoreException {
				for (ChatRoom room : getChatRooms()) {
					contentProvider.add(new ChannelUser(room), itemsFilter);
					for (ChatUser user : room.getUsers()) {
						contentProvider.add(user, itemsFilter);
					}
				}
			}
		};
		dialog.setHelpAvailable(false);
		configureDialog(dialog);

		final Image image = getImage();
		if (image != null) {
			dialog.setImage(image);
			shell.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					image.dispose();
				}
			});
		}

		if (Window.OK == dialog.open()) {
			Object selection[] = dialog.getResult();
			if (selection != null) {
				for (Object item : selection) {
					ChatUser participant = (ChatUser) item;
					participant.sendMessage(joinURLs(results.getSuccesses()));
				}
			}
		}
	}

	protected Image getImage() {
		Bundle bundle = Platform.getBundle("eclipseutils.ui.copyto");
		if (bundle != null) {
			URL url = FileLocator.find(bundle, new Path(
					"$nl$/icons/e16/copyto.png"), null);
			if (url != null) {
				ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
				return imageDesc.createImage();
			}
		}
		return null;
	}

	protected void configureDialog(SelectionStatusDialog dialog) {
		setMessage(dialog);
		setTitle(dialog);
	}

	protected void setTitle(SelectionStatusDialog dialog) {
		dialog.setTitle("Paste to IRC channel");
	}

	protected void setMessage(SelectionStatusDialog dialog) {
		dialog.setMessage("Select the user/channel that you want to message with the link.\nPress '?' to show all oder '#' to show only channels.");
	}

	private String joinURLs(final Collection<Result> results) {
		final StringBuffer sb = new StringBuffer();
		final Iterator<Result> it = results.iterator();
		while (it.hasNext()) {
			final Result result = it.next();
			if (result.getStatus().isOK()) {
				final String url = result.getStatus().getMessage();
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

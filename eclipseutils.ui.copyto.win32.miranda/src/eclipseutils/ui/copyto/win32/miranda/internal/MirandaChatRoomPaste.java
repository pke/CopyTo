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

import java.util.ArrayList;
import java.util.Collection;

import miranda.api.chat.dll.SESSION_INFO;

import com.sun.jna.examples.win32.ext.Visitor;

import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.chat.core.AbstractChatRoom;
import eclipseutils.ui.copyto.chat.core.ChatRoom;
import eclipseutils.ui.copyto.chat.core.ChatUser;
import eclipseutils.ui.copyto.chat.core.DefaultChatUser;
import eclipseutils.ui.copyto.chat.ui.AbstractChatRoomPaste;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class MirandaChatRoomPaste extends AbstractChatRoomPaste {

	private final class MirandaChatRoom extends AbstractChatRoom {
		private final SESSION_INFO item;

		private MirandaChatRoom(String name, SESSION_INFO item) {
			super(name);
			this.item = item;
		}

		public void sendMessage(String message) {
			mirandaIRC.sendMessage(getName(), message);
		}

		public Collection<ChatUser> getUsers() {
			Collection<ChatUser> users = new ArrayList<ChatUser>();
			for (String name : item.getUserNames()) {
				users.add(new DefaultChatUser(this, name));
			}
			return users;
		}
	}

	private final MirandaIRC mirandaIRC;

	public MirandaChatRoomPaste() {
		this.mirandaIRC = MirandaIRC.find();
	}

	@Override
	protected boolean canHandleResults(Results results) {
		return mirandaIRC != null;
	}

	protected Collection<ChatRoom> getChatRooms() {
		final Collection<ChatRoom> rooms = new ArrayList<ChatRoom>();
		mirandaIRC.visitSessions(new Visitor<SESSION_INFO>() {
			public boolean visit(final SESSION_INFO item) {
				ChatRoom room = new MirandaChatRoom(item.getName(), item);
				rooms.add(room);
				return true;
			}
		});
		return rooms;
	}
}

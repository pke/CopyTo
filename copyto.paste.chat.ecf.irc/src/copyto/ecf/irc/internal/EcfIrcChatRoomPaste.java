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
package copyto.ecf.irc.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.presence.chatroom.IChatRoomContainer;
import org.eclipse.ecf.presence.ui.chatroom.ChatRoomManagerView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;


import copyto.core.Results;
import copyto.paste.chat.core.AbstractChatRoom;
import copyto.paste.chat.core.ChatRoom;
import copyto.paste.chat.core.ChatUser;
import copyto.paste.chat.core.DefaultChatUser;
import copyto.paste.chat.ui.AbstractChatRoomPaste;


/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class EcfIrcChatRoomPaste extends AbstractChatRoomPaste {


	AtomicReference<IWorkbench> workbenchRef = new AtomicReference<IWorkbench>();
	private Collection<IChatRoomContainer> containers;

	void bind(IWorkbench workbench) {
		workbenchRef.set(workbench);
	}

	void unbind(IWorkbench workbench) {
		workbenchRef.compareAndSet(workbench, null);
	}

	@Override
	protected boolean canHandleResults(Results results) {
		IWorkbench workbench = workbenchRef.get();
		if (workbench != null) {
			containers = new ArrayList<IChatRoomContainer>();
			IViewReference[] refs = workbench.getActiveWorkbenchWindow()
					.getActivePage().getViewReferences();
			for (IViewReference ref : refs) {
				if (ref.getId().equals(ChatRoomManagerView.VIEW_ID)) {
					IViewPart viewPart = ref.getView(true);
					if (viewPart instanceof ChatRoomManagerView) {
						ChatRoomManagerView view = (ChatRoomManagerView) viewPart;
						containers.addAll(Arrays.asList(view
								.getChatRoomContainers()));
					}
				}
			}
		}
		return !containers.isEmpty();
	}

	@Override
	protected Collection<ChatRoom> getChatRooms() {
		Collection<ChatRoom> rooms = new ArrayList<ChatRoom>(containers.size());
		for (final IChatRoomContainer container : containers) {
		 new AbstractChatRoom(container.getConnectedID().getName()) {
				
				public void sendMessage(String message) {
					try {
						container.getChatRoomMessageSender().sendMessage(message);
					} catch (ECFException e) {
					}
				}
				
				public Collection<ChatUser> getUsers() {
					ID[] participants = container.getChatRoomParticipants();
					Collection<ChatUser> users = new ArrayList<ChatUser>(participants.length);
					for (ID id : participants) {
						users.add(new DefaultChatUser(this, id.getName()));
					}
					return users;
				}
			};
		}
		return rooms;
	}
	
	
}

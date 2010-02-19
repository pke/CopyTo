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
package eclipseutils.ui.copyto.chat.core;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class DefaultChatUser extends NamedModel implements ChatUser {

	private final ChatRoom room;

	public DefaultChatUser(ChatRoom room, String name) {
		super(name);
		this.room = room;
	}
	
	public ChatRoom getRoom() {
		return room;
	}
	
	public void sendMessage(String message) {
		room.sendMessage(getName() + ": " + message);
	}
}

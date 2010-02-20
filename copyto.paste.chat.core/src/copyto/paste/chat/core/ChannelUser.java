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
package copyto.paste.chat.core;

/**
 * A "channel" user is the channel itself.
 * 
 * <p>
 * It is used for searching the channel in the filter box.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class ChannelUser extends DefaultChatUser {

	public ChannelUser(ChatRoom room) {
		super(room, room.getName());		
	}
	
	@Override
	public void sendMessage(String message) {
		getRoom().sendMessage(message);
	}

}

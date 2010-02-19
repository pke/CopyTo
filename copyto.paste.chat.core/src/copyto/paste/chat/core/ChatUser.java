package copyto.paste.chat.core;

public interface ChatUser {
	String getName();
	
	ChatRoom getRoom();

	void sendMessage(String message);
}

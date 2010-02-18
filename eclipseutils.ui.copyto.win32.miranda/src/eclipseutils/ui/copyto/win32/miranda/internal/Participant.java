package eclipseutils.ui.copyto.win32.miranda.internal;



class Participant {
	private final String channel;
	private final String user;
	private MirandaIRC mirandaIRC;

	public Participant(MirandaIRC mirandaIRC, String channel, String user) {
		this.channel = channel;
		this.user = user;
		this.mirandaIRC = mirandaIRC;
	}

	public void sendMessage(String message) {
		mirandaIRC.sendMessage(channel, String.format("%s: %s", getUser(), message));
	}

	public String getChannel() {
		return channel;
	}

	public String getUser() {
		return user;
	}	
	
}
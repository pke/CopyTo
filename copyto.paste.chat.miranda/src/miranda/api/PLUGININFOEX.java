package miranda.api;

import java.util.UUID;

import com.sun.jna.Structure;

/**
 * from miranda\include\newpluginapi.h
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class PLUGININFOEX extends Structure {
	public static final UUID UNICODE_UUID = UUID.fromString("{0x9d6c3213, 0x2b4, 0x4fe1, { 0x92, 0xe6, 0x52, 0x6d, 0xe2, 0x4f, 0x8d, 0x65 }");
	
	public int cbSize;
	public /*char **/ int shortName;
	public int version;
	public /*char **/ int description;
	public /*char **/ int author;
	public /*char **/ int authorEmail;
	public /*char **/ int copyright;
	public /*char **/ int homepage;
	public byte flags; // right now the only flag, UNICODE_AWARE, is recognized here
	public int replacesDefaultModule;
    /***********  WILL BE DEPRECATED in 0.8 * *************/
	public UUID uuid; // Not required until 0.8.
	
	public boolean isUnicode() {
		return UNICODE_UUID.equals(uuid);
	}
}

package miranda.api.chat.dll;

import com.sun.jna.examples.win32.ext.ProcessAddressSpace;
import com.sun.jna.examples.win32.ext.ProcessStructure;
import com.sun.jna.examples.win32.W32API.HANDLE;


public class USERINFO extends ProcessStructure {
	public USERINFO(HANDLE process, int baseAddress) {
		super(process, baseAddress);
	}
	
	public USERINFO next() {
		if (next == 0) {
			return null;
		}
		return new USERINFO(getProcess(), next);
	}
	
	private String name;
	
	public String getName() {
		if (name == null) {
			name = ProcessAddressSpace.readStringW(getProcess(), pszNick);
		}
		return name;
	}
	
	public /*Pointer*/int pszNick;
	public /*Pointer*/int pszUID;
	public short Status;
	public int iStatusEx;
	public short ContactStatus;
	public /*Pointer*/int next;		
}
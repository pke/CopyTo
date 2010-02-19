package miranda.api.chat.dll;

import java.util.ArrayList;
import java.util.Collection;

import com.sun.jna.examples.win32.ext.ProcessAddressSpace;
import com.sun.jna.examples.win32.ext.ProcessStructure;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;




public class SESSION_INFO extends ProcessStructure {
	
	public SESSION_INFO(HANDLE process, int baseAddress) {
		super(process, baseAddress);
	}
	
	public Collection<USERINFO> getUsers() {
		Collection<USERINFO> users = new ArrayList<USERINFO>(nUsersInNicklist);
		USERINFO user = new USERINFO(getProcess(), pUsers);
		while (user != null) {
			users.add(user);
			user = user.next();
		}
		return users;
	}
	
	public Collection<String> getUserNames() {
		Collection<String> names = new ArrayList<String>(nUsersInNicklist);
		USERINFO user = new USERINFO(getProcess(), pUsers);
		while (user != null) {
			names.add(user.getName());
			user = user.next();
		}
		return names;
	}
	
	
	public HWND hWnd;

	public boolean bFGSet;
	public boolean bBGSet;
	public boolean bFilterEnabled;
	public boolean bNicklistEnabled;
	public boolean bInitDone;

	public /*Pointer*/int pszModule;
	public /*Pointer*/int ptszID;
	public /*Pointer*/int ptszName;
	public /*Pointer*/int ptszStatusbarText;
	public /*Pointer*/int ptszTopic;

	// I hate m3x, Unicode, IRC, chats etc...
	// #if defined( _UNICODE )
	public /*Pointer*/int pszID; // ugly fix for returning static ANSI strings in
							// GC_INFO
	public /*Pointer*/int pszName; // just to fix a bug quickly, should die after
							// porting
	// IRC to Unicode
	// #endif

	public int iType;
	public int iFG;
	public int iBG;
	public int iSplitterY;
	public int iSplitterX;
	public int iLogFilterFlags;
	public int nUsersInNicklist;
	public int iEventCount;
	public int iX;
	public int iY;
	public int iWidth;
	public int iHeight;
	public int iStatusCount;

	public short wStatus;
	public short wState;
	public short wCommandsNum;
	public int dwItemData;
	public int dwFlags;
	public int hContact;
	public HWND hwndStatus;
	public int LastTime;

	public /*Pointer*/int lpCommands;
	public /*Pointer*/int lpCurrentCommand;
	public /*Pointer*/int pLog;
	public /*Pointer*/int pLogEnd;
	public /*Pointer*/int pUsers;
	public /*Pointer*/int pMe;
	public /*Pointer*/int pStatuses;

	public HWND hwndTooltip;
	public int iOldItemID;

	public /*Pointer*/int next;

	private String name;

	public String getName() {
		if (name == null) {
			name = ProcessAddressSpace.readStringW(getProcess(), ptszName);
		}
		return name;
	}
}
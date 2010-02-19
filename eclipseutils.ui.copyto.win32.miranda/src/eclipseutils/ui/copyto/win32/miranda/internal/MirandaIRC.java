package eclipseutils.ui.copyto.win32.miranda.internal;

import static com.sun.jna.examples.win32.ComCtl32.TCIF_PARAM;
import static com.sun.jna.examples.win32.ComCtl32.TCIF_TEXT;
import static com.sun.jna.examples.win32.ComCtl32.TCM_GETITEM;
import static com.sun.jna.examples.win32.ComCtl32.TCM_GETITEMCOUNT;
import static com.sun.jna.examples.win32.User32.GWL_USERDATA;
import static com.sun.jna.examples.win32.User32.IDOK;
import static com.sun.jna.examples.win32.User32.WM_COMMAND;
import static com.sun.jna.examples.win32.User32.WM_SETTEXT;
import static com.sun.jna.examples.win32.User32.WM_USER;
import static com.sun.jna.examples.win32.Kernel32.PROCESS_VM_READ;
import static com.sun.jna.examples.win32.Kernel32.PROCESS_VM_WRITE;
import static com.sun.jna.examples.win32.Kernel32.PROCESS_QUERY_INFORMATION;
import static com.sun.jna.examples.win32.Kernel32.PROCESS_VM_OPERATION;
import static com.sun.jna.examples.win32.Kernel32.MEM_COMMIT;
import static com.sun.jna.examples.win32.Kernel32.MEM_RELEASE;
import static com.sun.jna.examples.win32.Kernel32.PAGE_READWRITE;
import miranda.api.chat.dll.SESSION_INFO;

import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.ext.ProcessAddressSpace;
import com.sun.jna.examples.win32.ext.ProcessWindowVisitor;
import com.sun.jna.examples.win32.ext.Windows;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.ext.Visitor;
import com.sun.jna.examples.win32.ComCtl32.TCITEM;
import com.sun.jna.examples.win32.ext.ProcessAddressSpace.SendMessageRunnable;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.examples.win32.W32API.SIZE_T;
import com.sun.jna.ptr.IntByReference;

public class MirandaIRC {
	private static final int IDC_TAB = 1074;
	// lParam = tabIndex
	private static int GC_SWITCHTAB = WM_USER + 137;
	private static int IDC_MESSAGE = 1009;
	private HWND window;
	private HANDLE process;
	private final static User32 user32 = User32.INSTANCE;
	private final static Kernel32 kernel32 = Kernel32.INSTANCE;

	public MirandaIRC(HWND window) {
		IntByReference processId = new IntByReference();
		user32.GetWindowThreadProcessId(window, processId);
		// TODO: Use DuplicateHandle here?
		this.process = kernel32.OpenProcess(PROCESS_VM_READ | PROCESS_VM_WRITE
				| PROCESS_QUERY_INFORMATION | PROCESS_VM_OPERATION, false,
				processId.getValue());
		this.window = window;
	}

	public void dispose() {
		if (process != null) {
			kernel32.CloseHandle(process);
			process = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public void visitSessions(final Visitor<SESSION_INFO> visitor) {
		visitTabs(TCIF_PARAM, new TabVisitor() {

			public boolean visit(TCITEM item, int index) {
				int lParam = item.lParam.intValue();
				if (lParam != 0) {
					SESSION_INFO info = new SESSION_INFO(process, lParam);
					visitor.visit(info);
				}
				return true;
			}

		});
	}

	public interface TabVisitor {
		boolean visit(TCITEM item, int index);
	}

	public void visitTabs(int mask, TabVisitor visitor) {
		final HWND tab = user32.GetDlgItem(window, IDC_TAB);
		int count = user32.SendMessage(tab, TCM_GETITEMCOUNT, 0, 0);
		if (count == 0) {
			return;
		}
		TCITEM item = new TCITEM();
		item.mask = mask;
		final ProcessAddressSpace mirandaSpace = new ProcessAddressSpace(
				process);
		int textMemory = 0;
		if ((mask & TCIF_TEXT) == TCIF_TEXT) {
			item.cchTextMax = 64;
			textMemory = kernel32.VirtualAllocEx(process, null, new SIZE_T(
					item.cchTextMax), MEM_COMMIT, PAGE_READWRITE);
			item.pszText = textMemory;
		}
		try {
			while (count-- > 0) {
				try {
					// A previous call could have reset the pszText pointer (see
					// API docs)
					if (textMemory != 0) {
						item.pszText = textMemory;
					}
					mirandaSpace.run(item, new SendMessageRunnable(tab,
							TCM_GETITEM, count));
					if (!visitor.visit(item, count)) {
						break;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} finally {
			mirandaSpace.dispose();
			if (textMemory != 0) {
				kernel32.VirtualFreeEx(process, textMemory, 0, MEM_RELEASE);
			}
		}
	}

	static SESSION_INFO sessionFromWindow(HANDLE process, HWND window) {
		int baseAddress = user32.GetWindowLong(window, GWL_USERDATA);
		if (baseAddress != 0) {
			return new SESSION_INFO(process, baseAddress);
		}
		return null;
	}

	private int findChannelTab(final String channel) {
		final int tabIndex[] = new int[] { -1 };
		visitTabs(TCIF_PARAM | TCIF_TEXT, new TabVisitor() {
			public boolean visit(TCITEM item, int index) {
				if (channel.equals(ProcessAddressSpace.readStringW(process,
						item.pszText))) {
					tabIndex[0] = index;
					return false;
				}
				return true;
			}
		});
		return tabIndex[0];
	}

	public void sendMessage(String channel, final String message) {
		final HWND messageWindow = user32.GetDlgItem(window, IDC_MESSAGE);
		if (user32.IsWindow(messageWindow)) {
			int tabIndex = findChannelTab(channel);
			if (tabIndex != -1) {
				user32.SendMessage(window, GC_SWITCHTAB, null, tabIndex);
				user32.SendMessage(messageWindow, WM_SETTEXT, 0, message);
				// This will enable the IDOK button
				user32.PostMessage(window, WM_COMMAND, IDC_MESSAGE, 0);
				// Whose enablement is checked when its command is received.
				user32.PostMessage(window, WM_COMMAND, IDOK, 0);
			}
		}
	}

	public static class MirandaWindowFinder extends ProcessWindowVisitor {

		private MirandaIRC found;

		public MirandaWindowFinder() {
			super("miranda32.exe");
		}

		@Override
		protected boolean visit(HANDLE process, String path, HWND window) {
			final HWND tab = user32.GetDlgItem(window, IDC_TAB);
			if (user32.IsWindow(tab)) {
				int result = user32.GetWindowLong(window, GWL_USERDATA);
				if (result != 0) {
					found = new MirandaIRC(window);
					return false;
				}
			}
			return true;
		}

		public MirandaIRC getFound() {
			return found;
		}
	}

	static public MirandaIRC find() {
		MirandaIRC.MirandaWindowFinder visitor = new MirandaWindowFinder();
		Windows.visitWindows(visitor);
		return visitor.getFound();
	}
}
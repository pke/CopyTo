package eclipseutils.ui.copyto.win32.miranda.internal;

import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.IntByReference;

import eclipseutils.ui.copyto.win32.miranda.internal.ProcessAddressSpace;
import eclipseutils.ui.copyto.win32.miranda.internal.SESSION_INFO;
import eclipseutils.ui.copyto.win32.miranda.internal.ProcessAddressSpace.SendMessageRunnable;


public class MirandaIRC {
	private static final int IDC_TAB = 1074;
	// lParam = tabIndex
	private static int GC_SWITCHTAB = User32.WM_USER + 137;
	private static int IDC_MESSAGE = 1009;
	private HWND window;
	private HANDLE process;

	public MirandaIRC(HWND window) {
		IntByReference processId = new IntByReference();
		User32.INSTANCE.GetWindowThreadProcessId(window, processId);
		// TODO: Use DuplicateHandle here?
		this.process = Kernel32.INSTANCE.OpenProcess(
				Kernel32.PROCESS_VM_READ | Kernel32.PROCESS_VM_WRITE
						| Kernel32.PROCESS_QUERY_INFORMATION
						| Kernel32.PROCESS_VM_OPERATION, false, processId
						.getValue());
		this.window = window;
	}

	public void dispose() {
		if (process != null) {
			Kernel32.INSTANCE.CloseHandle(process);
			process = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	public void visitTabs(Visitor<SESSION_INFO> visitor) {
		final HWND tab = User32.INSTANCE.GetDlgItem(window, IDC_TAB);
		int count = User32.INSTANCE.SendMessage(tab,
				User32.TCM_GETITEMCOUNT, 0, 0);
		if (count == 0) {
			return;
		}
		User32.TCITEM item = new User32.TCITEM();
		item.mask = User32.TCIF_PARAM;
		final ProcessAddressSpace mirandaSpace = new ProcessAddressSpace(
				process);
		try {
			while (count-- > 0) {
				try {
					mirandaSpace.run(item, new SendMessageRunnable(tab,
							User32.TCM_GETITEM, count));
				} catch (Throwable e) {
					e.printStackTrace();
				}
				int lParam = item.lParam.intValue();
				if (lParam != 0) {
					SESSION_INFO info = new SESSION_INFO(mirandaSpace
							.getProcess(), lParam);
					visitor.visit(info);
				}
			}
		} finally {
			mirandaSpace.dispose();
		}
	}

	static SESSION_INFO sessionFromWindow(HANDLE process, HWND window) {
		int baseAddress = User32.INSTANCE.GetWindowLong(window,
				User32.GWL_USERDATA);
		if (baseAddress != 0) {
			return new SESSION_INFO(process, baseAddress);
		}
		return null;
	}

	public void sendMessage(String channel, final String message) {
		final HWND messageWindow = User32.INSTANCE.GetDlgItem(window,
				IDC_MESSAGE);
		if (User32.INSTANCE.IsWindow(messageWindow)) {
			int tabIndex = 0;
			User32.INSTANCE.PostMessage(window, GC_SWITCHTAB, null,
					tabIndex);
			User32.INSTANCE.SendMessage(messageWindow, User32.WM_SETTEXT,
					0, message);
			// This will enable the IDOK button
			User32.INSTANCE.PostMessage(window, User32.WM_COMMAND,
					IDC_MESSAGE, 0);
			// Whose enablement is checked when its command is received.
			User32.INSTANCE.PostMessage(window, User32.WM_COMMAND,
					User32.IDOK, 0);
		}
	}

	public static class MirandaWindowFinder extends ProcessWindowVisitor {

		private MirandaIRC found;

		public MirandaWindowFinder() {
			super("miranda32.exe");
		}

		@Override
		protected boolean visit(HANDLE process, String path, HWND window) {
			final HWND tab = User32.INSTANCE.GetDlgItem(window, IDC_TAB);
			if (User32.INSTANCE.IsWindow(tab)) {
				int result = User32.INSTANCE.GetWindowLong(window,
						User32.GWL_USERDATA);
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
		Processes.enumWindow(visitor);
		return visitor.getFound();
	}
}
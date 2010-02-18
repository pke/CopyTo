package eclipseutils.ui.copyto.win32.miranda.internal;
import com.sun.jna.Native;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.Psapi;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.IntByReference;


public abstract class ProcessWindowVisitor implements Visitor<HWND> {
	private final char filename[] = new char[260];
	private final String processName;

	protected ProcessWindowVisitor(String processName) {
		this.processName = processName;
	}

	public boolean visit(HWND window) {
		IntByReference processId = new IntByReference();
		User32.INSTANCE.GetWindowThreadProcessId(window, processId);
		HANDLE process = Kernel32.INSTANCE.OpenProcess(
				Kernel32.PROCESS_VM_READ
						| Kernel32.PROCESS_QUERY_INFORMATION, false,
				processId.getValue());
		if (process == null) {
			return true;
		}
		try {
			Psapi.INSTANCE.GetModuleFileNameEx(process, null, filename,
					filename.length);
			String path = Native.toString(filename);
			if (matchesProcessName(path)) {
				return visit(process, path, window);
			}
		} finally {
			Kernel32.INSTANCE.CloseHandle(process);
		}
		return true;
	}

	protected boolean matchesProcessName(String name) {
		return name.endsWith(processName);
	}

	protected abstract boolean visit(HANDLE process, String path,
			HWND window);
}
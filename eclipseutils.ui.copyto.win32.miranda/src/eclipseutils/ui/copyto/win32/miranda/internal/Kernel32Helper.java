package eclipseutils.ui.copyto.win32.miranda.internal;

import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.ptr.PointerByReference;

public final class Kernel32Helper {

	public static void throwLastError(String message) throws Exception {
		int lastError = Kernel32.INSTANCE.GetLastError();
		if (lastError != 0) {
			throw new Exception(String.format("%s%s%s (%d)",
					message != null ? message : "",
					message != null ? ": " : "", getSystemError(lastError),
					lastError));
		}
	}

	public static void throwLastError() throws Exception {
		throwLastError(null);
	}

	public static String getSystemError(int code) {
		Kernel32 lib = Kernel32.INSTANCE;
		PointerByReference pref = new PointerByReference();
		try {
			lib.FormatMessage(Kernel32.FORMAT_MESSAGE_ALLOCATE_BUFFER
					| Kernel32.FORMAT_MESSAGE_FROM_SYSTEM
					| Kernel32.FORMAT_MESSAGE_IGNORE_INSERTS, null, code, 0,
					pref, 0, null);
			String s = pref.getValue().getString(0,
					!Boolean.getBoolean("w32.ascii"));
			s = s.replace(".\r", ".").replace(".\n", ".");
			return s;
		} finally {
			lib.LocalFree(pref.getValue());
		}
	}

	private Kernel32Helper() {
	}
}

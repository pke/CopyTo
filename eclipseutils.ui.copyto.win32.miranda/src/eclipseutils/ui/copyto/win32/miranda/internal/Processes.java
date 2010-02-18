package eclipseutils.ui.copyto.win32.miranda.internal;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.User32.WNDENUMPROC;
import com.sun.jna.examples.win32.W32API.HWND;


public class Processes {
	public static void enumWindow(final Visitor<HWND> visitor) {
		User32.INSTANCE.EnumWindows(new WNDENUMPROC() {
			public boolean callback(HWND hWnd, Pointer data) {
				return visitor.visit(hWnd);
			}
		}, null);
	}
}
package copyto.paste.chat.miranda;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.osgi.service.debug.DebugTrace;
import org.osgi.framework.FrameworkUtil;

import com.sun.jna.Pointer;

public class Trace implements DebugOptionsListener {
	
	private static boolean dump = false;
	private static DebugTrace trace;
	private final String symbolicName = FrameworkUtil.getBundle(Trace.class).getSymbolicName();
	
	public void optionsChanged(DebugOptions options) {
		if (trace == null) {
			trace = options.newDebugTrace(symbolicName);
		}
		dump = options.getBooleanOption(symbolicName + "/dump", false);
	}

	public static void dump(Pointer s, int size) {
		if (trace != null && dump) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("(%s): size=%d%n", s.toString(), size));
			byte bb[] = s.getByteArray(0, size);
			for (int i = 0; i < size; ++i) {
				sb.append(String.format("%02x%c", bb[i], ((i % 16) == 15) ? '\n' : ' '));
			}
			trace.trace(null, sb.toString());
		}
	}
}

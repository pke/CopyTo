package com.sun.jna.examples.win32.ext;

import com.sun.jna.Native;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.Psapi;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.IntByReference;

import static com.sun.jna.examples.win32.Kernel32.PROCESS_QUERY_INFORMATION;
import static com.sun.jna.examples.win32.Kernel32.PROCESS_VM_READ;

/**
 * A window visitor for processes that matches the name of the Windows process
 * module file and let the subclass deal with every matching process.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class ProcessWindowVisitor implements Visitor<HWND> {
	private final char filename[] = new char[260];
	private final String[] processNames;
	private IntByReference processId = new IntByReference();

	/**
	 * @param processNames
	 *            in lower-case of the processes that the subclass in interested
	 *            in.
	 */
	protected ProcessWindowVisitor(String... processNames) {
		this.processNames = processNames;
	}

	public boolean visit(HWND window) {
		User32.INSTANCE.GetWindowThreadProcessId(window, processId);
		HANDLE process = Kernel32.INSTANCE.OpenProcess(PROCESS_VM_READ
				| PROCESS_QUERY_INFORMATION, false, processId.getValue());
		if (process == null) {
			return true;
		}
		try {
			Psapi.INSTANCE.GetModuleFileNameEx(process, null, filename,
					filename.length);
			String path = Native.toString(filename).toLowerCase();
			if (matchesProcessName(path)) {
				return visit(process, path, window);
			}
		} finally {
			Kernel32.INSTANCE.CloseHandle(process);
		}
		return true;
	}

	/**
	 * Matches if one of the process names (given in the constructor) end with
	 * the given <i>name</i>.
	 * 
	 * <p>
	 * Subclasses may override to provide their own matching function.
	 * 
	 * @param name
	 *            of the process in lower case.
	 * @return
	 */
	protected boolean matchesProcessName(String name) {
		for (String processName : processNames) {
			if (name.endsWith(processName))
				return true;
		}
		return false;
	}

	protected abstract boolean visit(HANDLE process, String path, HWND window);
}
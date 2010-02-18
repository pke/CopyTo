package eclipseutils.ui.copyto.win32.miranda.internal;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.examples.win32.W32API.SIZE_T;
import com.sun.jna.examples.win32.W32API.WPARAM;
import com.sun.jna.ptr.IntByReference;

public class ProcessAddressSpace {
	private int memory = 0;
	private final HANDLE process;
	private static final SIZE_T zero = new SIZE_T(0);

	public interface ProcessRunnable<R> {
		R run(int memory) throws Throwable;
	}

	public static abstract class VoidProcessRunnable implements
			ProcessRunnable<Object> {
		public final Object run(int memory) throws Throwable {
			doRun(memory);
			return null;
		}

		protected abstract void doRun(int memory) throws Throwable;
	}

	public static class SendMessageRunnable extends VoidProcessRunnable {
		private final HWND window;
		private final int message;
		private final WPARAM wParam;

		public SendMessageRunnable(HWND window, int message, WPARAM wParam) {
			this.window = window;
			this.message = message;
			this.wParam = wParam;
		}

		public SendMessageRunnable(HWND window, int message, int wParam) {
			this(window, message, new WPARAM(wParam));
		}

		@Override
		protected void doRun(int memory) throws Throwable {
			User32.INSTANCE.SendMessage(window, message, wParam, memory);
		}
	}

	public ProcessAddressSpace(HANDLE process) {
		this.process = process;
	}

	public void dispose() {
		if (memory != 0) {
			if (!Kernel32.INSTANCE.VirtualFreeEx(process, memory, zero,
					Kernel32.MEM_RELEASE)) {
				throw new RuntimeException(Kernel32Helper
						.getSystemError(Kernel32.INSTANCE.GetLastError()));
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	private int getMemory(int size) {
		if (0 == memory) {
			this.memory = Kernel32.INSTANCE.VirtualAllocEx(process, null,
					new SIZE_T(size), Kernel32.MEM_RESERVE
							| Kernel32.MEM_COMMIT, Kernel32.PAGE_READWRITE);
		}
		return memory;
	}

	private static void dump(Pointer s, int size) {
		System.out.printf("(%s): size=%d%n", s.toString(), size);
		byte bb[] = s.getByteArray(0, size);
		for (int i = 0; i < size; ++i) {
			System.out.printf("%02x%c", bb[i], ((i % 16) == 15) ? '\n' : ' ');
		}
		System.out.println();
	}

	public <T extends Pointer, R> R run(T pointer, int itemSize,
			ProcessRunnable<R> runnable) throws Throwable {
		SIZE_T nSize = new SIZE_T(itemSize);
		IntByReference written = new IntByReference();
		boolean success = Kernel32.INSTANCE.WriteProcessMemory(process,
				getMemory(itemSize), pointer, nSize, written);
		dump(pointer, itemSize);
		if (!success || written.getValue() != nSize.intValue()) {
			return null;
		}
		
		Memory testBuffer = new Memory(itemSize);
		success = Kernel32.INSTANCE.ReadProcessMemory(
				process, memory, testBuffer, nSize, written);		
		dump(testBuffer, itemSize);
		return runnable.run(memory);
	}

	public <S extends Structure, R> R run(final S structure,
			final ProcessRunnable<R> runnable) throws Throwable {

		structure.write();

		R result = run(structure.getPointer(), structure.size(),
				new ProcessRunnable<R>() {

					public R run(int memory) throws Throwable {
						R result = runnable.run(memory);

						IntByReference read = new IntByReference();
						boolean success = Kernel32.INSTANCE.ReadProcessMemory(
								process, memory, structure.getPointer(),
								new SIZE_T(structure.size()), read);
						if (!success || read.getValue() != structure.size()) {
							return null;
						}
						structure.read();
						return result;
					}
				});

		return result;
	}

	public HANDLE getProcess() {
		return process;
	}
}
package com.sun.jna.examples.win32.ext;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.Kernel32Helper;
import com.sun.jna.examples.win32.User32;
import com.sun.jna.examples.win32.Kernel32Helper.FunctionCall;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.examples.win32.W32API.SIZE_T;
import com.sun.jna.examples.win32.W32API.WPARAM;
import com.sun.jna.ptr.IntByReference;

import eclipseutils.ui.copyto.win32.miranda.internal.Trace;

/**
 * Allows code to be run inside a remote process address space.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ProcessAddressSpace {
	public interface ProcessRunnable<R> {
		R run(int memory) throws Throwable;
	}

	public static class SendMessageRunnable implements ProcessRunnable<Integer> {
		private final int message;
		private final HWND window;
		private final WPARAM wParam;

		public SendMessageRunnable(HWND window, int message, int wParam) {
			this(window, message, new WPARAM(wParam));
		}

		public SendMessageRunnable(HWND window, int message, WPARAM wParam) {
			this.window = window;
			this.message = message;
			this.wParam = wParam;
		}

		public Integer run(int memory) throws Throwable {
			return User32.INSTANCE.SendMessage(window, message, wParam, memory);
		}
	}

	public static abstract class VoidProcessRunnable implements
			ProcessRunnable<Object> {
		public final Object run(int memory) throws Throwable {
			doRun(memory);
			return null;
		}

		protected abstract void doRun(int memory) throws Throwable;
	}

	private final class StructureReader<S extends Structure, R> implements
			ProcessRunnable<R> {
		private final ProcessRunnable<R> runnable;
		private final S structure;

		private StructureReader(ProcessRunnable<R> runnable, S structure) {
			this.runnable = runnable;
			this.structure = structure;
		}

		public R run(final int memory) throws Throwable {
			R result = runnable.run(memory);

			try {
				Kernel32Helper.checkedCall(true, new FunctionCall<Boolean>() {
					public Boolean call() {
						IntByReference read = new IntByReference();
						return Kernel32.INSTANCE.ReadProcessMemory(process,
								memory, structure.getPointer(), new SIZE_T(
										structure.size()), read)
								&& read.getValue() == structure.size();
					}
				});

			} catch (RuntimeException e) {
				return null;
			}
			structure.read();
			return result;
		}
	}

	private int lastSize = 0;

	private int memory = 0;

	private final HANDLE process;

	public ProcessAddressSpace(HANDLE process) {
		this.process = process;
	}

	public void dispose() {
		freeMemory();
	}

	public HANDLE getProcess() {
		return process;
	}

	public <S extends Structure, R> R run(final S structure,
			final ProcessRunnable<R> runnable) throws Throwable {

		structure.write();

		R result = run(structure.getPointer(), structure.size(),
				new StructureReader<S, R>(runnable, structure));

		return result;
	}

	public <T extends Pointer, R> R run(final T pointer, final int itemSize,
			ProcessRunnable<R> runnable) throws Throwable {
		if (lastSize != itemSize) {
			freeMemory();
		}
		lastSize = itemSize;

		Trace.dump(pointer, itemSize);
		try {
			Kernel32Helper.checkedCall(true, new FunctionCall<Boolean>() {
				public Boolean call() {
					final SIZE_T nSize = new SIZE_T(itemSize);
					IntByReference written = new IntByReference();
					boolean success = Kernel32.INSTANCE.WriteProcessMemory(
							process, getMemory(itemSize), pointer, nSize,
							written)
							&& written.getValue() == itemSize;
					return success;
				}
			});
		} catch (RuntimeException e) {
			return null;
		}

		return runnable.run(memory);
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
		super.finalize();
	}

	private void freeMemory() {
		if (memory != 0) {
			try {
				Kernel32Helper.checkedVirtualFreeEx(process, memory,
						Kernel32.MEM_RELEASE);
			} finally {
				memory = 0;
			}
		}
	}

	private int getMemory(int size) {
		if (0 == memory) {
			this.memory = Kernel32.INSTANCE.VirtualAllocEx(process, null,
					new SIZE_T(size), Kernel32.MEM_COMMIT,
					Kernel32.PAGE_READWRITE);
		}
		return memory;
	}

	public static String readStringA(HANDLE process, int address) {
		return readString(process, address, 1);
	}

	/**
	 * Reads a null terminated string from the given address.
	 * 
	 * <p>
	 * This method assumes that the string at the address is a UNICODE (2 byte
	 * per character) string.
	 * 
	 * <p>
	 * The method reads the <i>process</i> memory using
	 * <code>ReadProcessMemory</code> and if it does not encounter a
	 * 0-terminating character it might read memory that it has no access to. In
	 * that case the returned string is automatically cut.
	 * 
	 * 
	 * @param process
	 *            to read the string from.
	 * @param address
	 *            in the process' address space to read the string from.
	 * @return the string at at the given <i>address</i>.
	 */
	public static String readStringW(HANDLE process, int address) {
		return readString(process, address, 2);
	}

	private static String readString(HANDLE process, int address, int charSize) {
		if (charSize != 1 && charSize != 2) {
			throw new IllegalArgumentException("Character size must be 1 or 2.");
		}
		StringBuffer buffer = new StringBuffer();
		IntByReference read = new IntByReference();
		int offset = address;
		CharByReference c = new CharByReference();
		final Kernel32.SIZE_T size = new Kernel32.SIZE_T(charSize);
		while (true) {
			if (!Kernel32.INSTANCE.ReadProcessMemory(process, offset, c, size,
					read)
					|| read.getValue() != charSize) {
				break;
			}
			if (c.getValue() == 0) {
				break;
			}
			buffer.append(c.getValue());
			offset += charSize;
		}
		return buffer.toString();
	}
}
package eclipseutils.ui.copyto.win32.miranda.internal;

import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.ptr.IntByReference;

public abstract class ProcessStructure extends Structure {

	private final HANDLE process;

	public ProcessStructure(HANDLE process, int baseAddress) {
		this.process = process;
		Memory memory = new Memory(size());
		IntByReference read = new IntByReference();
		Kernel32.INSTANCE.ReadProcessMemory(process, baseAddress, memory,
				new Kernel32.SIZE_T(memory.getSize()), read);
		useMemory(memory);
		read();
	}

	protected HANDLE getProcess() {
		return process;
	}
	
	protected String readStringW(int address) {
		StringBuffer buffer = new StringBuffer();
		IntByReference read = new IntByReference();
		int offset = address;
		CharByReference c = new CharByReference();
		Kernel32.SIZE_T size = new Kernel32.SIZE_T(2);
		while (true) {
			if (!Kernel32.INSTANCE.ReadProcessMemory(getProcess(), offset, c, size,
					read) || read.getValue() != 2) {
				break;
			}
			if (c.getValue() == 0) {
				break;
			}
			buffer.append(c.getValue());
			offset += 2;
		}
		return buffer.toString();
	}
}
package com.sun.jna.examples.win32.ext;

import com.sun.jna.Memory;
import com.sun.jna.Structure;
import com.sun.jna.examples.win32.Kernel32;
import com.sun.jna.examples.win32.W32API.HANDLE;
import com.sun.jna.ptr.IntByReference;

/**
 * Base structure for a structure that will be constructed in via
 * ReadProcessMemory.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
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

	/**
	 * @return the process associated with this structure.
	 */
	protected final HANDLE getProcess() {
		return process;
	}
}
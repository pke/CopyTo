/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package com.sun.jna.examples.win32.ext;

import com.sun.jna.examples.win32.Kernel32;
import static com.sun.jna.examples.win32.Kernel32.TH32CS_SNAPMODULE;
import com.sun.jna.examples.win32.Kernel32.MODULEENTRY32W;
import com.sun.jna.examples.win32.W32API.HANDLE;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class Processes {

	/**
	 * Allows the walking over entries of a snapshot.
	 * 
	 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
	 *
	 * @param <T> type of entry to walk over.
	 */
	public interface SnapshotWalker<T> {
		/**
		 * @return One of the Kernel32.TH32CS* flags.
		 */
		int getFlag();

		/**
		 * Gets the next entry for the snapshot.
		 * 
		 * @param snapshot
		 * @param entry
		 *            will be <code>null</code> for the first entry.
		 * @return the next entry in the snapshot that follows <i>entry</i> or
		 *         <code>null</code> if <i>entry</i> was the last entry.
		 */
		T next(HANDLE snapshot, T entry);
	}

	private static class ModuleSnapshotWalker implements
			SnapshotWalker<MODULEENTRY32W> {

		public MODULEENTRY32W next(HANDLE snapshot, MODULEENTRY32W entry) {
			if (null == entry) {
				entry = new MODULEENTRY32W();
				if (!Kernel32.INSTANCE.Module32First(snapshot, entry)) {
					return null;
				}
			} else {
				if (!Kernel32.INSTANCE.Module32Next(snapshot, entry)) {
					return null;
				}
			}
			return entry;
		}

		public int getFlag() {
			return TH32CS_SNAPMODULE;
		}
	}

	public static <T> void visitSnapshot(int processId,
			SnapshotWalker<T> walker, Visitor<T> visitor) {
		HANDLE snapshot = Kernel32.INSTANCE.CreateToolhelp32Snapshot(walker
				.getFlag(), processId);
		try {
			T entry = walker.next(snapshot, null);
			if (entry != null) {
				do {
					if (!visitor.visit(entry)) {
						break;
					}
				} while ((entry = walker.next(snapshot, entry)) != null);
			}
		} finally {
			Kernel32.INSTANCE.CloseHandle(snapshot);
		}
	}

	public static void visitModules(int processId,
			Visitor<MODULEENTRY32W> visitor) {
		visitSnapshot(processId, new ModuleSnapshotWalker(), visitor);
	}

	public static void visitModules(HANDLE process,
			Visitor<MODULEENTRY32W> visitor) {
		visitModules(Kernel32.INSTANCE.GetProcessId(process), visitor);
	}

	private Processes() {
	}
}

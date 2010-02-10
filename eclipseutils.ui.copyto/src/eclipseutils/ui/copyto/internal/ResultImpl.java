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
package eclipseutils.ui.copyto.internal;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Result;

class ResultImpl implements Result {

	private final Copyable copyable;
	private final URL location;
	private final IStatus status;
	private final long timestamp;

	public ResultImpl(final Copyable copyable, final URL location,
			final IStatus status) {
		this.copyable = copyable;
		this.location = location;
		this.status = status;
		this.timestamp = System.currentTimeMillis();
	}

	public ResultImpl(final Copyable copyable, final URL location) {
		this(copyable, location, Status.OK_STATUS);
	}

	public ResultImpl(final Copyable copyable, final Throwable throwable) {
		this(copyable, null, new Status(IStatus.ERROR,
				HttpCopyToHandler.symbolicName, "Failed to copy", throwable)); //$NON-NLS-1$
	}

	public String getTargetName() {
		return null;
	}

	public Copyable getCopyable() {
		return this.copyable;
	}

	public URL getLocation() {
		return this.location;
	}

	public long getTimeStamp() {
		return this.timestamp;
	}

	public IStatus getStatus() {
		return this.status;
	}

}
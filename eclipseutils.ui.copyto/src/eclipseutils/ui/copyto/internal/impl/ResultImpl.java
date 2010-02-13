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
package eclipseutils.ui.copyto.internal.impl;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.internal.HttpCopyToHandler;
import eclipseutils.ui.copyto.internal.api.Target;

public class ResultImpl implements Result {

	private final Copyable copyable;
	private final URL location;
	private final IStatus status;
	private final long timestamp;
	private final Target target;

	public ResultImpl(final Copyable copyable, final Target target,
			final URL location, final IStatus status) {
		this.copyable = copyable;
		this.location = location;
		this.target = target;
		this.status = status;
		this.timestamp = System.currentTimeMillis();
	}

	public ResultImpl(final Copyable copyable, final Target target,
			final URL location) {
		this(copyable, target, location, Status.OK_STATUS);
	}

	public ResultImpl(final Copyable copyable, final Target target,
			final Throwable throwable) {
		this(copyable, target, null, new Status(IStatus.ERROR,
				HttpCopyToHandler.symbolicName, "Failed to copy", throwable)); //$NON-NLS-1$
	}

	public Target getTarget() {
		return target;
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
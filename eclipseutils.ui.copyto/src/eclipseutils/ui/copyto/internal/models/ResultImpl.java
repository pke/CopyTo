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
package eclipseutils.ui.copyto.internal.models;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.internal.services.HttpProtocol;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ResultImpl implements Result {

	private final URL location;
	private final IStatus status;
	private final long timestamp;
	private final Results parent;
	private final Copyable copyable;

	/**
	 * @param parent
	 * @param copyable
	 * @param location
	 * @param status
	 */
	public ResultImpl(final Results parent, final Copyable copyable,
			final URL location, final IStatus status) {
		this.parent = parent;
		this.copyable = copyable;
		this.location = location;
		this.status = status;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * @param parent
	 * @param location
	 */
	public ResultImpl(final Results parent, final Copyable copyable,
			final URL location) {
		this(parent, copyable, location, Status.OK_STATUS);
	}

	/**
	 * @param parent
	 * @param copyable
	 * @param throwable
	 */
	public ResultImpl(final Results parent, final Copyable copyable,
			final Throwable throwable) {
		this(parent, copyable, null, new Status(IStatus.ERROR,
				HttpProtocol.symbolicName, "Failed to copy", throwable)); //$NON-NLS-1$
	}

	public Results getParent() {
		return parent;
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

	public Copyable getCopyable() {
		return copyable;
	}

}
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
package copyto.target.http.core.internal;

import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

import copyto.core.Copyable;
import copyto.core.Result;
import copyto.core.Results;

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
		this(parent, copyable, location, new Status(IStatus.OK, FrameworkUtil
				.getBundle(copyable.getClass()).getSymbolicName(), location.toString()));
	}

	/**
	 * @param parent
	 * @param copyable
	 * @param throwable
	 */
	public ResultImpl(final Results parent, final Copyable copyable,
			final Throwable throwable) {
		this(parent, copyable, null, new Status(IStatus.ERROR, FrameworkUtil
				.getBundle(copyable.getClass()).getSymbolicName(),
				"Failed to copy", throwable)); //$NON-NLS-1$
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
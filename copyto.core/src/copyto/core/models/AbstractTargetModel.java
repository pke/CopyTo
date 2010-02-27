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
package copyto.core.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.Preferences;

import osgiutils.services.LogHelper;
import osgiutils.services.ServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import copyto.core.Protocol;
import copyto.core.ProtocolDescriptor;
import copyto.core.ProtocolRegistry;
import copyto.core.ResultHandler;
import copyto.core.Results;
import copyto.core.Target;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class AbstractTargetModel extends AbstractModel implements
		Target, Serializable {
	private static final long serialVersionUID = -395321611927968738L;
	private transient final Protocol protocol;

	public AbstractTargetModel(Protocol protocol) {
		id = UUID.randomUUID().toString();
		name = "unnamed"; //$NON-NLS-1$
		this.protocol = protocol;
	}

	public void load(final Preferences preferences) {
		setName(preferences.get("label", getName()));

		final String protocolId = preferences.get("protocol.id", null);
		if (protocolId != null) {
			ProtocolDescriptor desc = Trackers
					.run(
							ProtocolRegistry.class,
							new ServiceRunnable<ProtocolRegistry, ProtocolDescriptor>() {
								public ProtocolDescriptor run(
										ProtocolRegistry service) {
									return service.find(protocolId);
								}
							});
		}
	}

	/**
	 * @param preferences
	 */
	public void save(final Preferences preferences) {
		preferences.put("label", name); //$NON-NLS-1$
	}

	/**
	 * @return BASE64 encoded string
	 */
	public String toBase64() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(this);
			return new String(Base64.encodeBase64(out.toByteArray()));
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param base64Encoding
	 * @return target
	 * @throws Exception
	 */
	public static AbstractTargetModel valueOf(final String base64Encoding)
			throws Exception {
		final Object target = new ObjectInputStream(new ByteArrayInputStream(
				Base64.decodeBase64(base64Encoding.getBytes()))).readObject();
		if (target instanceof AbstractTargetModel) {
			((AbstractTargetModel) target).id = UUID.randomUUID().toString();
			return (AbstractTargetModel) target;
		}
		return null;
	}

	private String id;
	private String name;

	private transient IStatus connectionStatus = new Status(IStatus.OK, "test", //$NON-NLS-1$
			"Not tested yet"); //$NON-NLS-1$

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(final String name) {
		pcs.firePropertyChange("name", this.name, //$NON-NLS-1$
				this.name = name);
	}

	// From JavadocConfigurationBlock
	private boolean checkURLConnection(URL url) {
		int res = 0;
		URLConnection connection = null;
		try {
			connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				connection.connect();
				res = ((HttpURLConnection) connection).getResponseCode();
			}
			InputStream is = null;
			try {
				is = connection.getInputStream();
				byte[] buffer = new byte[256];
				while (is.read(buffer) != -1) {
				}
			} finally {
				if (is != null)
					is.close();
			}
		} catch (IllegalArgumentException e) {
			return false; // workaround for bug 91072
		} catch (NullPointerException e) {
			return false; // workaround for
							// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6536522
		} catch (IOException e) {
			return false;
		}
		return res < 400;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * @param connectionStatus
	 */
	public void setConnectionStatus(final IStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	/**
	 * @return status
	 */
	public IStatus getConnectionStatus() {
		return connectionStatus;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	private void notifyListeners(final Results results) {
		Trackers.runAll(ResultHandler.class,
				new SimpleServiceRunnable<ResultHandler>() {
					@Override
					protected void doRun(final ResultHandler service) {
						try {
							service.handleResults(results);
						} catch (final Throwable t) {
							LogHelper.error(t, "Calling result handler"); //$NON-NLS-1$
						}
					}
				});
	}
}
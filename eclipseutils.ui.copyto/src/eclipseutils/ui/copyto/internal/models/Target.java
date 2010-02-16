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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Target extends PlatformObject implements Serializable {
	private static final long serialVersionUID = -395321611927968738L;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	protected void firePropertyChange(final String propertyName,
			final Object oldValue, final Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	/**
	 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
	 * 
	 */
	public interface IconLoader {
		/**
		 * @param descriptor
		 */
		void iconLoaded(ImageDescriptor descriptor);
	}

	interface IconResponse {
		void iconLoaded(ImageDescriptor icon);
	}

	/**
	 * @return an icon
	 */
	public ImageDescriptor getIcon() {
		try {
			// Do we have a local version cached?
			// Simple return that

			final URL iconUrl = new URL("http://" + new URL(getUrl()).getHost() //$NON-NLS-1$
					+ "/favicon.ico"); //$NON-NLS-1$
			@SuppressWarnings("unused")
			final ImageDescriptor imageDesc = ImageDescriptor
					.createFromURL(iconUrl);
		} catch (final MalformedURLException e) {
		}
		return null;
	}

	/**
	 * 
	 */
	public Target() {
		id = UUID.randomUUID().toString();
		name = "unnamed"; //$NON-NLS-1$
		uri = "http://"; //$NON-NLS-1$
	}

	/**
	 * @param node
	 * @param path
	 */
	public Target(final Preferences node, final IPath path) {
		id = node.name();
		name = node.get("label", null); //$NON-NLS-1$
		uri = node.get("url", null); //$NON-NLS-1$

		try {
			if (node.nodeExists("params")) { //$NON-NLS-1$
				final Preferences paramsNode = node.node("params"); //$NON-NLS-1$
				for (final String key : paramsNode.keys()) {
					additionalParams.put(key, paramsNode.get(key, "")); //$NON-NLS-1$
				}
			}
		} catch (final BackingStoreException e) {
		}
	}

	static class TargetWorkenchAdapter extends WorkbenchAdapter {

		private static TargetWorkenchAdapter instance;

		static IWorkbenchAdapter getInstance() {
			if (instance == null) {
				instance = new TargetWorkenchAdapter();
			}

			return instance;
		}

		@Override
		public String getLabel(final Object object) {
			return ((Target) object).getName();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter) {
		if (adapter == IWorkbenchAdapter.class) {
			return TargetWorkenchAdapter.getInstance();
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @param node
	 */
	public void save(final Preferences node) {
		node.put("label", name); //$NON-NLS-1$
		node.put("url", uri); //$NON-NLS-1$
		if (!additionalParams.isEmpty()) {
			final Preferences paramsNode = node.node("params"); //$NON-NLS-1$
			for (final Entry<String, String> entry : additionalParams
					.entrySet()) {
				paramsNode.put(entry.getKey(), entry.getValue());
			}
		}
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
		}
		return null;
	}

	/**
	 * @param base64Encoding
	 * @return target
	 * @throws Exception
	 */
	public static Target valueOf(final String base64Encoding) throws Exception {
		final Object target = new ObjectInputStream(new ByteArrayInputStream(
				Base64.decodeBase64(base64Encoding.getBytes()))).readObject();
		if (target instanceof Target) {
			return (Target) target;
		}
		return null;
	}

	final String id;
	private String name;
	private String uri;
	private final Map<String, String> additionalParams = new HashMap<String, String>();
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
		propertyChangeSupport.firePropertyChange("name", this.name, //$NON-NLS-1$
				this.name = name);
	}

	/**
	 * @return url
	 */
	public String getUrl() {
		return uri;
	}

	/**
	 * @param url
	 */
	public void setUrl(final String uri) {
		propertyChangeSupport.firePropertyChange("url", this.uri, //$NON-NLS-1$
				this.uri = uri);
	}

	/**
	 * @return params
	 */
	public Map<String, String> getAdditionalParams() {
		return additionalParams;
	}

	/**
	 * 
	 */
	public void testConnection() {
		try {
			final URL url = new URL(getUrl());
			final URLConnection connection = url.openConnection();
			connection.connect();
			setConnectionStatus(Status.OK_STATUS);
		} catch (final Exception e) {
			setConnectionStatus(new Status(IStatus.ERROR, "test", "Error", e)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	public String toString() {
		return getName() + "@" + getUrl(); //$NON-NLS-1$
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

}
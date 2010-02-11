package eclipseutils.ui.copyto.internal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class Target extends PlatformObject implements Serializable {
	private static final long serialVersionUID = -395321611927968738L;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

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

	public Target() {
		id = UUID.randomUUID().toString();
		name = "unnamed";
		url = "http://";
	}

	public Target(final Preferences node) {
		id = node.name();
		name = node.get("label", null);
		url = node.get("url", null);
		try {
			if (node.nodeExists("params")) {
				final Preferences paramsNode = node.node("params");
				for (final String key : paramsNode.keys()) {
					additionalParams.put(key, paramsNode.get(key, ""));
				}
			}
		} catch (final BackingStoreException e) {
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter) {
		return super.getAdapter(adapter);
	}

	public void save(final Preferences node) {
		node.put("label", name);
		node.put("url", url);
		if (!additionalParams.isEmpty()) {
			final Preferences paramsNode = node.node("params");
			for (final Entry<String, String> entry : additionalParams
					.entrySet()) {
				paramsNode.put(entry.getKey(), entry.getValue());
			}
		}
	}

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
	private String url;
	private final Map<String, String> additionalParams = new HashMap<String, String>();
	private transient IStatus connectionStatus = new Status(IStatus.OK, "test",
			"Not tested yet");

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		propertyChangeSupport.firePropertyChange("name", this.name,
				this.name = name);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		propertyChangeSupport.firePropertyChange("url", this.url,
				this.url = url);
	}

	public Map<String, String> getAdditionalParams() {
		return additionalParams;
	}

	public void testConnection() {
		try {
			final URL url = new URL(getUrl());
			final URLConnection connection = url.openConnection();
			connection.connect();
			setConnectionStatus(Status.OK_STATUS);
		} catch (final Exception e) {
			setConnectionStatus(new Status(IStatus.ERROR, "test", "Error", e));
		}
	}

	public boolean isVisible() {
		return true;
	}

	public void setVisible(final boolean visible) {

	}

	public void setConnectionStatus(final IStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public IStatus getConnectionStatus() {
		return connectionStatus;
	}
}
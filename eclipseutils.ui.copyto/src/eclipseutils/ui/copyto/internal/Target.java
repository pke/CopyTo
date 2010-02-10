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

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}

	public Target() {
		id = UUID.randomUUID().toString();
		label = "unnamed";
		url = "http://";
	}

	public Target(Preferences node) {
		id = node.name();
		label = node.get("label", null);
		url = node.get("url", null);
		try {
			if (node.nodeExists("params")) {
				Preferences paramsNode = node.node("params");
				for (String key : paramsNode.keys()) {
					additionalParams.put(key, paramsNode.get(key, ""));
				}
			}
		} catch (BackingStoreException e) {
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return super.getAdapter(adapter);
	}

	public void save(Preferences node) {
		node.put("label", label);
		node.put("url", url);
		if (!additionalParams.isEmpty()) {
			Preferences paramsNode = node.node("params");
			for (Entry<String, String> entry : additionalParams.entrySet()) {
				paramsNode.put(entry.getKey(), entry.getValue());
			}
		}
	}

	public String toBase64() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(out);
			outputStream.writeObject(this);
			return new String(Base64.encodeBase64(out.toByteArray()));
		} catch (IOException e) {
		}
		return null;
	}

	public static Target valueOf(String base64Encoding) throws Exception {
		Object target = new ObjectInputStream(new ByteArrayInputStream(Base64
				.decodeBase64(base64Encoding.getBytes()))).readObject();
		if (target instanceof Target) {
			return (Target) target;
		}
		return null;
	}

	final String id;
	String label;
	private String url;
	private final Map<String, String> additionalParams = new HashMap<String, String>();
	private transient IStatus connectionStatus = new Status(IStatus.OK, "test",
			"Not tested yet");

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		propertyChangeSupport.firePropertyChange("label", this.label,
				this.label = label);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		propertyChangeSupport.firePropertyChange("url", this.url,
				this.url = url);
	}

	public Map<String, String> getAdditionalParams() {
		return additionalParams;
	}

	public void testConnection() {
		try {
			URL url = new URL(getUrl());
			URLConnection connection = url.openConnection();
			connection.connect();
			setConnectionStatus(Status.OK_STATUS);
		} catch (Exception e) {
			setConnectionStatus(new Status(IStatus.ERROR, "test", "Error", e));
		}
	}

	public void setConnectionStatus(IStatus connectionStatus) {
		this.connectionStatus = connectionStatus;
	}

	public IStatus getConnectionStatus() {
		return connectionStatus;
	}
}
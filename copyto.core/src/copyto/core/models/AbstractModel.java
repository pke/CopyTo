package copyto.core.models;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.runtime.PlatformObject;

public abstract class AbstractModel extends PlatformObject {

	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(
			final PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	/**
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(final String propertyName,
			final PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(final String propertyName,
			final Object oldValue, final Object newValue) {
		pcs.firePropertyChange(propertyName, oldValue, newValue);
	}

}
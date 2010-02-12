package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Control;

import eclipseutils.ui.copyto.internal.preferences.ControlCreator;

/**
 * Abstract base class for control creators.
 * 
 * <p>
 * It provides helpful methods for control creation.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class AbstractControlCreator implements ControlCreator {
	/**
	 * Sets a tooltip on a control.
	 * 
	 * @param control
	 * @param bean
	 * @param property
	 */
	protected void setToolTip(final Control control, final Object bean,
			final String property) {
		final String desc = LocalizationHelper.getDescription(bean, property);
		if (desc != null) {
			control.setToolTipText(desc);
		}
	}
}
package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Control;

import eclipseutils.ui.copyto.internal.preferences.ControlCreator;

abstract class AbstractControlCreator implements ControlCreator {
	protected void setToolTip(final Control control, final Object bean,
			final String property) {
		final String desc = LocalizationHelper.getDescription(bean, property);
		if (desc != null) {
			control.setToolTipText(desc);
		}
	}
}
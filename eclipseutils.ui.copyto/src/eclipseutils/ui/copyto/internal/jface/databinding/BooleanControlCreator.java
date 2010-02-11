package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eclipseutils.ui.copyto.internal.preferences.ControlCreator;

public class BooleanControlCreator extends AbstractControlCreator {

	private static BooleanControlCreator instance;

	/**
	 * @return the singleton instance of this creator
	 */
	static public ControlCreator getInstance() {
		if (null == instance) {
			instance = new BooleanControlCreator();
		}
		return instance;
	}

	protected BooleanControlCreator() {
	}

	public IObservableValue createControl(final Composite parent,
			final Object bean, final String property) {
		final Button control = new Button(parent, SWT.CHECK);
		control.setText(LocalizationHelper.getLabel(bean, property));
		setToolTip(control, bean, property);
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(
				control);
		return SWTObservables.observeSelection(control);
	}
}
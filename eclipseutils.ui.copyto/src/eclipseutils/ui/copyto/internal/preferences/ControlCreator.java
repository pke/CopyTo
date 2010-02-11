package eclipseutils.ui.copyto.internal.preferences;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

public interface ControlCreator {
	IObservableValue createControl(Composite parent, Object bean,
			String property);
}
package eclipseutils.ui.copyto.internal.preferences;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.widgets.Composite;

import eclipseutils.ui.copyto.internal.jface.databinding.CreatorToolkit;

public interface ControlCreator {
	IObservableValue create(CreatorToolkit toolkit, Composite parent, Object bean, String property);
}
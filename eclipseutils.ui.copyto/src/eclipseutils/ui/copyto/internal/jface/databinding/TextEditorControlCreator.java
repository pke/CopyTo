package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class TextEditorControlCreator extends LabelControlCreator {

	@Override
	IObservableValue createEditor(final CreatorToolkit toolkit,
			final Composite parent, final Object bean, final String property) {
		final Text text = toolkit.createText(parent, null, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		return SWTObservables.observeText(text, SWT.Modify);
	}
}
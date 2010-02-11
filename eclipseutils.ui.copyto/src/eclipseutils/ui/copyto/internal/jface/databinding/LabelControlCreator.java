package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

abstract class LabelControlCreator extends AbstractControlCreator {

	public IObservableValue createControl(final Composite parent,
			final Object bean, final String property) {
		final Label label = new Label(parent, SWT.LEFT);
		label.setText(LocalizationHelper.getLabel(bean, property) + ":");
		setToolTip(label, bean, property);
		GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(
				label);
		final IObservableValue editorValue = createEditor(parent, bean,
				property);
		return editorValue;
	}

	abstract IObservableValue createEditor(Composite parent, Object bean,
			String property);
}
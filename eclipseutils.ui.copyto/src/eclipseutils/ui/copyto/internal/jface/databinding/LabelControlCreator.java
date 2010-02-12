package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

abstract class LabelControlCreator extends AbstractControlCreator {

	public IObservableValue create(final CreatorToolkit toolkit,
			final Composite parent, final Object bean, final String property) {
		final Label label = toolkit.createLabel(parent, LocalizationHelper
				.getLabel(bean, property)
				+ ":", SWT.LEFT);
		setToolTip(label, bean, property);
		GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(
				label);
		final IObservableValue editorValue = createEditor(toolkit, parent,
				bean, property);
		return editorValue;
	}

	abstract IObservableValue createEditor(CreatorToolkit toolkit,
			Composite parent, Object bean, String property);
}
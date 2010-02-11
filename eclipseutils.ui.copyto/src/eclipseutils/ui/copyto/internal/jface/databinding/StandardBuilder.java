package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class StandardBuilder extends AbstractBuilder {

	private final DataBindingContext ctx;

	public StandardBuilder(final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		super(parent, bean, targetToModelPolicy);
		ctx = new DataBindingContext();
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				ctx.dispose();
			}
		});
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(parent);
	}

	@Override
	protected void bindValue(final IObservableValue target,
			final IObservableValue model,
			final UpdateValueStrategy targetToModel) {
		final Binding binding = ctx.bindValue(target, model, targetToModel,
				null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
	}

	public Builder updateModels() {
		ctx.updateModels();
		return this;
	}

	public Builder addDialogSupport(final TitleAreaDialog dialog,
			final IObservableValue target) {
		final AggregateValidationStatus status = new AggregateValidationStatus(
				ctx, AggregateValidationStatus.MAX_SEVERITY);
		if (target != null) {
			status.addChangeListener(new IChangeListener() {
				public void handleChange(final ChangeEvent event) {
					final Object value = status.getValue();
					if (value instanceof IStatus) {
						target.setValue(value);
					}
				}
			});
		}
		TitleAreaDialogSupport.create(dialog, ctx);
		return this;
	}
}
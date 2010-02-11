package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class BuiltDialog extends TitleAreaDialog implements
		BuilderProvider {

	private Builder builder;

	public BuiltDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite client = new Composite((Composite) super
				.createDialogArea(parent), SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(client);
		builder = createBuilder(client).addDialogSupport(this,
				new AbstractObservableValue() {
					public Object getValueType() {
						return null;
					}

					@Override
					protected Object doGetValue() {
						return null;
					}

					@Override
					protected void doSetValue(final Object value) {
						if (value instanceof IStatus) {
							final IStatus status = (IStatus) value;
							final Button button = getButton(IDialogConstants.OK_ID);
							if (button != null && !button.isDisposed()) {
								button.setEnabled(status.getSeverity() != IStatus.ERROR);
							}
						}
					}
				});
		return client;
	}

	@Override
	protected void okPressed() {
		builder.updateModels();
		super.okPressed();
	}
}

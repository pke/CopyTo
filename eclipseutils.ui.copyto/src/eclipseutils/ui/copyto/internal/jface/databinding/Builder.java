package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.dialogs.TitleAreaDialog;

public interface Builder {
	Builder field(String property);

	Builder field(final String property, IValidator afterConvertValidator);

	Builder field(String property, UpdateValueStrategy targetToModel);

	Builder newLine();

	Builder addDialogSupport(final TitleAreaDialog dialog,
			final IObservableValue target);

	Builder updateModels();
}
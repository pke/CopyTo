package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.dialogs.TitleAreaDialog;

/**
 * A builder for creating a UI.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Builder {
	Builder field(String property);

	Builder field(String property, FieldOptions fieldOptions);

	Builder newLine();

	Builder addDialogSupport(TitleAreaDialog dialog, IObservableValue target);

	Builder updateModels();
}
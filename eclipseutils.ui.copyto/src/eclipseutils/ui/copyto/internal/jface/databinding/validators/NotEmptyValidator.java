package eclipseutils.ui.copyto.internal.jface.databinding.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class NotEmptyValidator extends AbstractValidator {

	private final boolean trim;
	private static IValidator trimInstance;
	private static IValidator instance;

	public static IValidator getInstance(final boolean trimBeforeValidate) {
		if (trimBeforeValidate) {
			if (trimInstance == null) {
				trimInstance = new NotEmptyValidator(true);
			}
			return trimInstance;
		} else {
			if (instance == null) {
				instance = new NotEmptyValidator(false);
			}
			return instance;
		}
	}

	/**
	 * @param trimBeforeValidate
	 *            whether the string should be trimmed first before validation
	 *            of the length takes place.
	 */
	protected NotEmptyValidator(final boolean trimBeforeValidate) {
		super(IStatus.ERROR);
		this.trim = trimBeforeValidate;
	}

	@Override
	protected String performValidation(final Object value) {
		if (value instanceof String) {
			final String string = (String) value;
			final int len = trim ? string.trim().length() : string.length();
			if (len == 0) {
				return "Cannot be empty";
			}
		}
		return null;
	}
}

package eclipseutils.ui.copyto.internal.jface.databinding.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * Negates a validator.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class NotValidator extends AbstractValidator {

	private final IValidator validator;
	private final String message;

	public NotValidator(final IValidator validator, final String message,
			final int severity) {
		super(severity);
		this.validator = validator;
		this.message = message;
	}

	@Override
	protected String performValidation(final Object value) throws Throwable {
		final IStatus status = safeValidate(validator, value, getSeverity());
		if (status.isOK()) {
			return message;
		}
		return null;
	}
}

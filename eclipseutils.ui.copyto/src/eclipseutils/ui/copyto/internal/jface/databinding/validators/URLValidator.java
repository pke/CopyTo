package eclipseutils.ui.copyto.internal.jface.databinding.validators;

import java.net.URL;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class URLValidator extends AbstractValidator {

	private static IValidator instance;

	/**
	 * @return the singleton instance of the validator.
	 */
	public static IValidator getInstance() {
		if (instance == null) {
			instance = new URLValidator();
		}
		return instance;
	}

	protected URLValidator() {
		super(IStatus.ERROR);
	}

	@Override
	protected String performValidation(final Object value) throws Throwable {
		new URL(value.toString());
		return null;
	}
}
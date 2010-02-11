package eclipseutils.ui.copyto.internal.jface.databinding.validators;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;

public class ListValidator extends AbstractValidator {

	private final Collection<?> collection;

	public ListValidator(final Collection<?> collection) {
		this(collection, IStatus.ERROR);
	}

	public ListValidator(final Collection<?> collection, final int severity) {
		super(severity);
		this.collection = collection;
	}

	@Override
	protected String performValidation(final Object value) {
		if (!collection.contains(value)) {
			return "not contained in list";
		}
		return null;
	}
}

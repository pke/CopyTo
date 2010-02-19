/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package eclipseutils.jface.databinding.validators;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class AbstractValidator implements IValidator {

	private final int severity;

	/**
	 * @param severity
	 */
	public AbstractValidator(final int severity) {
		this.severity = severity;
	}

	public IStatus validate(final Object value) {
		try {
			final String error = performValidation(value);
			if (error != null && error.length() > 0) {
				return result(error, severity);
			}
		} catch (final Throwable t) {
			return result(t.getMessage(), severity);
		}

		return Status.OK_STATUS;
	}

	protected int getSeverity() {
		return severity;
	}

	/**
	 * 
	 * @param value
	 * @return the error message or <code>null</code> if there was no error
	 * @throws Throwable
	 */
	protected abstract String performValidation(Object value) throws Throwable;

	protected static IStatus result(final String message, final int severity) {
		return new Status(severity, FrameworkUtil.getBundle(
				AbstractValidator.class).getSymbolicName(), message);
	}

	protected static IStatus safeValidate(final IValidator validator,
			final Object value, final int severity) {
		try {
			return validator.validate(value);
		} catch (final Throwable t) {
			return result(t.getMessage(), severity);
		}
	}

}
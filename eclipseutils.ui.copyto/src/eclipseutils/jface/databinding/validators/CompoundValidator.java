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


/**
 * Contains at least two validators that are all validated together.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class CompoundValidator implements IValidator {

	private final IValidator[] validators;
	private final int severity;

	public CompoundValidator(final IValidator validator,
			final IValidator... validatorParams) {
		this(IStatus.ERROR, validator, validatorParams);
	}

	/**
	 * @param severity
	 *            the IStatus severity to report this validation error with.
	 * @param validator
	 *            first validator
	 * @param validatorParams
	 *            remaining validators
	 */
	public CompoundValidator(final int severity, final IValidator validator,
			final IValidator... validatorParams) {
		this.severity = severity;
		this.validators = new IValidator[validatorParams.length + 1];
		this.validators[0] = validator;
		System.arraycopy(validatorParams, 0, this.validators, 1,
				validatorParams.length);
	}

	public IStatus validate(final Object value) {
		for (final IValidator validator : validators) {
			final IStatus status = AbstractValidator.safeValidate(validator,
					value, severity);
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}
}

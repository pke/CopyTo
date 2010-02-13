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

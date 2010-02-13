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
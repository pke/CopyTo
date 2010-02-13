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

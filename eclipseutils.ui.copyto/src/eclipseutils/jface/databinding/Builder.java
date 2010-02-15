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
package eclipseutils.jface.databinding;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.dialogs.TitleAreaDialog;

/**
 * A builder for creating a UI.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Builder {
	/**
	 * Shortcut for {@link #field(String, FieldOptions)}.
	 * 
	 * @param property
	 * @return this builder for chaining
	 */
	Builder field(String property);

	/**
	 * 
	 * @param property
	 * @param fieldOptions
	 * @return this builder for chaining
	 */
	Builder field(String property, FieldOptions fieldOptions);

	/**
	 * 
	 * @return this builder for chaining
	 */
	Builder newLine();

	/**
	 * 
	 * @param dialog
	 * @param target
	 * @return this builder for chaining
	 */
	Builder addDialogSupport(TitleAreaDialog dialog, IObservableValue target);

	/**
	 * Manually refreshing the models from the targets (controls).
	 * 
	 * @return this builder for chaining
	 */
	Builder updateModels();
}
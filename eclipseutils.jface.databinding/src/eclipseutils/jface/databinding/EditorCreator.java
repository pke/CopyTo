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
import org.eclipse.swt.widgets.Composite;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface EditorCreator {
	/**
	 * @param toolkit
	 * @param parent
	 * @param bean
	 * @param property
	 * @param style additional style flags (i.e. SWT.READ_ONLY)
	 * @param fieldOptions TODO
	 * @return an observable to bind to.
	 */
	IObservableValue create(ControlToolkit toolkit, Composite parent,
			Object bean, String property, int style, FieldOptions fieldOptions);

	/**
	 * @return whether or not the editor created by this creator has a label nor
	 *         not.
	 */
	boolean hasLabel();
}
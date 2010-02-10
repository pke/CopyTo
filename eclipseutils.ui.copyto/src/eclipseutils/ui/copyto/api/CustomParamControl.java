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
package eclipseutils.ui.copyto.api;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.widgets.Composite;

import eclipseutils.ui.copyto.CustomExtensionParamControl;

/**
 * Allows the creation of a custom control for a parameter.
 * 
 * <p>
 * The implementation can also implement {@link IExecutableExtension} to get its
 * configuration element for setting the label and tooltip.
 * 
 * <p>
 * Exemplary use might be a control that regenerates an API key to gain access
 * to a web service.
 * 
 * Implementors should inherit from {@link CustomExtensionParamControl}, as it
 * provides easy access to the contributions label and description attributes.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * @since 0.1
 * @see CustomExtensionParamControl
 */
public interface CustomParamControl {
	/**
	 * Creates a custom control for a parameter.
	 * 
	 * @param parent
	 *            of the control to create. The parent has a default GridLayout
	 *            set with no margins.
	 * 
	 * @return an observable for the created control. Must never return
	 *         <code>null</code>.
	 */
	IObservableValue createControl(Composite parent);
}

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
package eclipseutils.ui.copyto;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import eclipseutils.ui.copyto.api.CustomParamControl;

/**
 * Abstract base class for {@link CustomParamControl} implementations based on
 * extensions.
 * 
 * <p>
 * Implementors should use this class to read the label and description
 * extension attributes.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class CustomExtensionParamControl implements
		CustomParamControl, IExecutableExtension {
	private String labelText;
	private String desc;

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.labelText = config.getAttribute("label"); //$NON-NLS-1$
		if (labelText == null) {
			throw new CoreException(new Status(IStatus.ERROR, config
					.getContributor().getName(), NLS.bind(
					"No label specified for {}", config.getAttribute("name")))); //$NON-NLS-1$//$NON-NLS-2$
		}
		this.desc = config.getAttribute("description"); //$NON-NLS-1$
	}

	/**
	 * @return the label text. Never null.
	 */
	public String getLabelText() {
		return labelText;
	}

	/**
	 * @return the description or an empty string.
	 */
	public String getDescription() {
		return desc != null ? desc : ""; //$NON-NLS-1$
	}
}

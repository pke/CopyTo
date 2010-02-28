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
package eclipseutils.core.extensions;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class BaseExtensionDescriptor extends PlatformObject {
	private static final String CLASS_ATT = "class";
	private static final String NAME_ATT = "name";
	private static final String ID_ATT = "id";

	private final IConfigurationElement configElement;

	public BaseExtensionDescriptor(IConfigurationElement configElement) {
		this.configElement = configElement;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == IConfigurationElement.class) {
			return configElement;
		}
		return super.getAdapter(adapter);
	}

	public String getAttribute(String name) {
		return configElement.getAttribute(name);
	}

	public String getId() {
		return configElement.getAttribute(ID_ATT);
	}

	public String getName() {
		return configElement.getAttribute(NAME_ATT);
	}

	public <T> T createExecutableExtension() throws CoreException {
		return createExecutableExtension(CLASS_ATT);
	}

	@SuppressWarnings("unchecked")
	public <T> T createExecutableExtension(String name) throws CoreException {
		return (T) configElement.createExecutableExtension(name);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BaseExtensionDescriptor) {
			return obj instanceof BaseExtensionDescriptor
					&& getId().equals(((BaseExtensionDescriptor) obj).getId());
		} else if (obj instanceof IAdaptable) {
			IConfigurationElement other = (IConfigurationElement) ((IAdaptable)obj).getAdapter(IConfigurationElement.class);
			if (other != null) {
				return configElement.equals(other);
			}
		} else if (obj instanceof IConfigurationElement) {
			return configElement.equals(obj);
		}
		
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return getName();
	}

	public URL getFileLocation(String name) {
		String path = getAttribute(name);
		if (path != null) {
			return FileLocator.find(Platform.getBundle(configElement
					.getContributor().getName()), new Path(path), null);
		}
		return null;
	}
}

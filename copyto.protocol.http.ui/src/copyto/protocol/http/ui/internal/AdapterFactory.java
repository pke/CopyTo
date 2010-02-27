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
package copyto.protocol.http.ui.internal;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.FrameworkUtil;

import copyto.protocol.http.core.HttpTarget;
import eclipseutils.jface.databinding.BuilderAdapter;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class AdapterFactory implements IAdapterFactory {
	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof HttpTarget) {
			if (adapterType == BuilderAdapter.class) {
				return new HttpBuilderAdapter();
			} else if (adapterType == ImageDescriptor.class) {
				URL url = FileLocator.find(FrameworkUtil.getBundle(getClass()),
						new Path("$nl$/icons/web.png"), null);
				if (url != null) {
					return ImageDescriptor.createFromURL(url);
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

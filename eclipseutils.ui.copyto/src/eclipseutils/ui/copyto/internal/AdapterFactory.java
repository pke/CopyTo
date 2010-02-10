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
package eclipseutils.ui.copyto.internal;

import java.net.URL;

import org.eclipse.core.runtime.IAdapterFactory;

import eclipseutils.ui.copyto.api.json.MapResponse;

public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adaptableObject instanceof MapResponse) {
			try {
				return new URL(((MapResponse) adaptableObject).getMap().get(
						"url").toString()); //$NON-NLS-1$
			} catch (final Exception e) {
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

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
package copyto.protocol.http.core.internal.json;

import java.net.URL;

import org.eclipse.core.runtime.IAdapterFactory;



/**
 * Factory that provides a URL for JSON MapResponse types.
 * 
 * It tries to extracts an URL from the JSON Map.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
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

	/**
	 * Not used for extension declared factories
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

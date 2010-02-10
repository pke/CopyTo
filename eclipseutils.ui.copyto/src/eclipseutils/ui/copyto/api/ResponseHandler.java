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

import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;

/**
 * 
 * @author <a href="mailto:kursawe@topsystem.de">Philipp Kursawe</a>
 * 
 */
public interface ResponseHandler {
	/**
	 * Retrieves a location URL from a HTTP method response.
	 * 
	 * @param method
	 *            to use for extracting the location URL.
	 * @return a valid URL object
	 * @throws Exception
	 *             if there was an error creating a location URL from the given
	 *             <code>method</code>.
	 */
	URL getLocation(HttpMethod method) throws Exception;
}

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
package copyto.target.http.core.internal.responses;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

import copyto.target.http.core.ResponseHandler;

/**
 * (Default) ResonseHandler that handles 302-Redirect and 201-created responses.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class RedirectResponseHandler implements ResponseHandler {

	public URL getLocation(final HttpMethod method) throws Exception {
		if (HttpStatus.SC_MOVED_TEMPORARILY == method.getStatusCode()
				|| HttpStatus.SC_CREATED == method.getStatusCode()) {
			final Header locationHeader = method.getResponseHeader("Location"); //$NON-NLS-1$
			String value = locationHeader.getValue();
			if (value.charAt(0) == '/') {
				value = "http://" + method.getRequestHeader("Host").getValue() + value; //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new URL(value);
		}
		throw new IOException("Response did not contain a redirect location"); //$NON-NLS-1$
	}

	public RedirectResponseHandler() {
	}
}

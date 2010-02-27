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

package copyto.target.pastebin.com.internal;

import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;

import copyto.protocol.http.core.ResponseHandler;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class PasteBinResponseHandler implements ResponseHandler {

	public URL getLocation(HttpMethod method) throws Exception {
		return new URL(method.getResponseBodyAsString());
	}

}

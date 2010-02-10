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
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.ResponseHandler;
import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.responses.RedirectResponseHandler;

/**
 * Copies the content of Copyable to a HTTP destination using POST.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class HttpCopyToHandler implements Handler {

	public static final String symbolicName = FrameworkUtil.getBundle(
			HttpCopyToHandler.class).getSymbolicName();

	private ResponseHandler responseHandler;

	private ResponseHandler getResponseHandler() {
		if (null == responseHandler) {
			try {
				final IConfigurationElement[] configurationElements = Platform
						.getExtensionRegistry().getConfigurationElementsFor(
								HttpCopyToHandler.symbolicName,
								CopyToHandler.COMMAND_TARGET_PARAM, getId());
				for (final IConfigurationElement configurationElement : configurationElements) {
					if ("responseHandler".equals(configurationElement.getName())) { //$NON-NLS-1$
						responseHandler = (ResponseHandler) configurationElement
								.createExecutableExtension("class"); //$NON-NLS-1$
						break;
					}
				}
			} catch (final Exception e) {
				// Catches ClassCastException and CoreException
			}
			if (null == responseHandler) {
				responseHandler = new RedirectResponseHandler();
			}
		}
		return responseHandler;
	}

	protected abstract HttpMethod getMethod();

	public Result copy(final Copyable copyable,
			final Map<String, String> params, final IProgressMonitor monitor) {
		final HttpMethod method = getMethod();

		for (Entry<String, String> entry : params.entrySet()) {
			final String name = entry.getKey();
			if (method instanceof PostMethod) {
				((PostMethod) method).addParameter(name, entry.getValue());
			}
		}

		final HttpClient httpClient = new HttpClient();

		try {
			final int status = httpClient.executeMethod(method);
			// TODO: Handle status here
			final URL location = getResponseHandler().getLocation(method);
			return new ResultImpl(copyable, location);
		} catch (final Throwable t) {
			return new ResultImpl(copyable, t);
		}
	}

}

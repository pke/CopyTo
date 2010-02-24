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
package copyto.ui.internal.services;

import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.osgi.framework.FrameworkUtil;


import copyto.core.Copyable;
import copyto.core.HttpResponseHandler;
import copyto.core.Target;
import copyto.core.responses.RedirectResponseHandler;
import copyto.ui.internal.Messages;
import copyto.ui.internal.models.TargetModel;

import osgiutils.services.LogHelper;

/**
 * Copies the content of Copyable to a HTTP destination using POST.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class HttpProtocol {

	private static final String COMMAND_TARGETS_PARAM = "targets"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String symbolicName = FrameworkUtil.getBundle(
			HttpProtocol.class).getSymbolicName();

	final private HttpClient httpClient = new HttpClient();

	private static HttpResponseHandler getResponseHandler(final Target target) {
		final IConfigurationElement[] elements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						"copyto.core", COMMAND_TARGETS_PARAM,
						target.getId());
		for (final IConfigurationElement element : elements) {
			try {
				if ("responseHandler".equals(element.getName())) { //$NON-NLS-1$
					return (HttpResponseHandler) element
							.createExecutableExtension("class"); //$NON-NLS-1$
				}
			} catch (final ClassCastException e) {
				LogHelper
						.error(
								e,
								"Class returned from %s is not an instance of %s", element.getContributor().getName(), HttpResponseHandler.class.getName()); //$NON-NLS-1$
			} catch (final CoreException e) {
				LogHelper
						.error(
								e,
								"Could not instantiate response handler from %s", element.getContributor().getName()); //$NON-NLS-1$
			}

		}
		return RedirectResponseHandler.getInstance();
	}

	/**
	 * @param results
	 * @param copyable
	 * @param target
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public URL copy(final Copyable copyable, final TargetModel target,
			final IProgressMonitor monitor) throws Exception {

		final URI uri = new URI(resolveParams(copyable, target.getUrl()), false);

		final PostMethod method = new PostMethod();
		method.setURI(uri);

		final String[] pairs = uri.getQuery().split("&"); //$NON-NLS-1$
		final NameValuePair params[] = new NameValuePair[pairs.length];
		for (int i = 0; i < pairs.length; ++i) {
			int equalIndex = pairs[i].indexOf('=');
			params[i] = new NameValuePair(pairs[i].substring(0, equalIndex),
					equalIndex != -1 ? pairs[i].substring(equalIndex+1) : ""); //$NON-NLS-1$
		}
		method.setRequestBody(params);

		try {
			LogHelper.debug("Sending paste to %s", method.getPath());
			final int status = httpClient.executeMethod(method);
			for (Header header : method.getRequestHeaders()) {
				LogHelper.debug("%s: %s", header.getName(), header.getValue());
			}
			LogHelper.debug(
					"Response: %d - %s", status, HttpStatus.getStatusText(status)); //$NON-NLS-1$
			for (Header header : method.getResponseHeaders()) {
				LogHelper.debug("%s: %s", header.getName(), header.getValue());
			}
			return getResponseHandler(target).getLocation(method);
		} finally {
			method.releaseConnection();
		}
	}

	private static String resolveParams(final Copyable copyable,
			final String text) throws CoreException {
		final IStringVariableManager variableManager = VariablesPlugin
				.getDefault().getStringVariableManager();
		final IValueVariable vars[] = {
				variableManager
						.newValueVariable(
								"copyto.source", Messages.CopyServiceImpl_SourceVar, true, copyable.getSource().getClass().getName()), //$NON-NLS-1$
				variableManager
						.newValueVariable(
								"copyto.text", Messages.CopyServiceImpl_TextVar, true, copyable.getText()), //$NON-NLS-1$
				variableManager
						.newValueVariable(
								"copyto.mime-type", Messages.CopyServiceImpl_MimeTypeVar, true, copyable.getMimeType()) }; //$NON-NLS-1$

		// Make sure they are not registered
		variableManager.removeVariables(vars);
		try {
			variableManager.addVariables(vars);
		} catch (final CoreException e) {
		}

		try {
			return variableManager.performStringSubstitution(text, false);
		} finally {
			variableManager.removeVariables(vars);
		}
	}
}

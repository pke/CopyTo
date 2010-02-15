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
package eclipseutils.ui.copyto.internal.impl;

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

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.ResponseHandler;
import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.internal.LogHelper;
import eclipseutils.ui.copyto.internal.api.Target;
import eclipseutils.ui.copyto.responses.RedirectResponseHandler;

/**
 * Copies the content of Copyable to a HTTP destination using POST.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class HttpCopyToHandler {

	private static final String COMMAND_TARGETS_PARAM = "targets"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static final String symbolicName = FrameworkUtil.getBundle(
			HttpCopyToHandler.class).getSymbolicName();

	final private HttpClient httpClient = new HttpClient();

	private static ResponseHandler getResponseHandler(final Target target) {
		final IConfigurationElement[] elements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						HttpCopyToHandler.symbolicName, COMMAND_TARGETS_PARAM,
						target.getId());
		for (final IConfigurationElement element : elements) {
			try {
				if ("responseHandler".equals(element.getName())) { //$NON-NLS-1$
					return (ResponseHandler) element
							.createExecutableExtension("class"); //$NON-NLS-1$
				}
			} catch (final ClassCastException e) {
				LogHelper
						.error(
								e,
								"Class returned from %s is not an instance of %s", element.getContributor().getName(), ResponseHandler.class.getName()); //$NON-NLS-1$
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
	 * @param copyable
	 * @param target
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	public Result copy(final Copyable copyable, final Target target,
			final IProgressMonitor monitor) throws Exception {

		final URI uri = new URI(resolveParams(copyable, target.getUrl()), false);

		final PostMethod method = new PostMethod();
		method.setURI(uri);

		final String[] pairs = uri.getQuery().split("&"); //$NON-NLS-1$
		final NameValuePair params[] = new NameValuePair[pairs.length];
		for (int i = 0; i < pairs.length; ++i) {
			final String[] keyValue = pairs[i].split("="); //$NON-NLS-1$
			params[i] = new NameValuePair(keyValue[0],
					keyValue.length == 1 ? "" : keyValue[1]); //$NON-NLS-1$
		}
		method.setRequestBody(params);

		try {
			final int status = httpClient.executeMethod(method);
			LogHelper.debug(
					"HTTP: Response: %s", HttpStatus.getStatusText(status)); //$NON-NLS-1$
			return new ResultImpl(copyable, target, getResponseHandler(target)
					.getLocation(method));
		} catch (final Throwable t) {
			return new ResultImpl(copyable, target, t);
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

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
package copyto.protocol.http.core.internal;

import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

import osgiutils.services.LogHelper;
import copyto.core.Copyable;
import copyto.core.Protocol;
import copyto.core.Results;
import copyto.core.Target;
import copyto.core.TargetParam;
import copyto.protocol.http.core.HttpTarget;

/**
 * Copies the content of Copyable to a HTTP destination using POST.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class HttpProtocol implements Protocol, IExecutableExtension {
	
	final private HttpClient httpClient = new HttpClient();
	private IConfigurationElement config;
	
	
	/**
	 * @param results
	 * @param copyable
	 * @param target
	 * @param monitor
	 * @return
	 * @throws Exception
	 */
	private URL copy(final Copyable copyable, HttpTarget target,
			final IProgressMonitor monitor) throws Exception {

		//final URI uri = new URI(resolveParams(copyable, target.getUrl()), false);

		final PostMethod method = new PostMethod(target.getHost());
		//method.setURI(uri);
		
		
		final NameValuePair params[] = new NameValuePair[target.getParams().size()];
		int i=0;
		for (TargetParam<?> param : target.getParams()) {
			params[i++] = new NameValuePair(param.getName(), resolveParams(copyable, param.getValue().toString()));
		}
		/*final String[] pairs = uri.getQuery().split("&"); //$NON-NLS-1$
		final NameValuePair params[] = new NameValuePair[pairs.length];
		for (int i = 0; i < pairs.length; ++i) {
			int equalIndex = pairs[i].indexOf('=');
			params[i] = new NameValuePair(pairs[i].substring(0, equalIndex),
					equalIndex != -1 ? pairs[i].substring(equalIndex + 1) : ""); //$NON-NLS-1$
		}*/
		method.setRequestBody(params);

		try {
			LogHelper.debug("Sending paste to %s", method.getPath());
			final int status = httpClient.executeMethod(method);
			for (Header header : method.getRequestHeaders()) {
				LogHelper.debug("%s: %s", header.getName(), header.getValue());
			}
			LogHelper
					.debug(
							"Response: %d - %s", status, HttpStatus.getStatusText(status)); //$NON-NLS-1$
			for (Header header : method.getResponseHeaders()) {
				LogHelper.debug("%s: %s", header.getName(), header.getValue());
			}
			return target.getResponseHandler().getLocation(method);
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

	public Results transfer(IProgressMonitor monitor, HttpTargetModel target,
			Copyable... copyables) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor,
				copyables.length);

		final Results results = new Results(target);

		for (final Copyable copyable : copyables) {
			try {
				results.add(new ResultImpl(results, copyable, copy(copyable,
						target, subMonitor)));
			} catch (final Exception e) {
				results.add(new ResultImpl(results, copyable, e));
			}
		}
		return results;
	}

	public Target createTarget() {
		return new HttpTargetModel(this);
	}

	public String getId() {
		return this.config.getAttribute("id");
	}
	
	public String getName() {
		return this.config.getAttribute("name");
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}
}

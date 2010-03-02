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
package copyto.target.http.core.internal;

import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import copyto.core.Copyable;
import copyto.core.Persistable;
import copyto.core.Results;
import copyto.core.TargetFactory;
import copyto.core.TargetParam;
import copyto.core.models.AbstractTargetModel;
import copyto.core.models.TargetParamsModel;
import copyto.target.http.core.HttpTarget;
import copyto.target.http.core.ResponseHandler;
import copyto.target.http.core.ResponseHandlerDescriptor;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class HttpTargetModel extends AbstractTargetModel implements HttpTarget {
	private static final long serialVersionUID = 3069654670412516519L;

	private String host;
	private ResponseHandlerDescriptor responseHandlerDesc;
	private TargetParamsModel params = new TargetParamsModel();
	
	public HttpTargetModel(TargetFactory desc) {
		super(desc);
		host = "http://pastebin.com";
	}

	public void setHost(String host) {
		firePropertyChange("host", this.host, this.host = host);
	}

	public String getHost() {
		return host;
	}
	
	public String getSummary() {
		return getHost();
	}

	@Override
	public void load(Preferences preferences) {
		super.load(preferences);
		setHost(preferences.get("host", getHost()));

		try {
			String id = preferences.get("responseHandler", null);
			responseHandlerDesc = ResponseHandlers.getInstance().find(id);
			if (responseHandlerDesc != null) {
				if (preferences.nodeExists("responseHandler")) {
					try {
						ResponseHandler handler = responseHandlerDesc.createResponseHandler();
						if (handler instanceof Persistable) {
							((Persistable)handler).load(preferences.node("responseHandler"));
						}
					} catch (CoreException e) {
					}
				}
			}
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		params.load(preferences);
	}

	public void setResponseHandlerDescriptor(
			ResponseHandlerDescriptor responseHandlerDesc) {
		firePropertyChange("responseHandlerDescriptor",
				this.responseHandlerDesc,
				this.responseHandlerDesc = responseHandlerDesc);
	}

	public ResponseHandlerDescriptor getResponseHandlerDescriptor() {
		return responseHandlerDesc;
	}

	@Override
	public void save(Preferences preferences) {
		super.save(preferences);
		preferences.put("host", getHost());
		if (responseHandlerDesc != null) {
			preferences.put("responseHandler", responseHandlerDesc.getId());
			Preferences handlerNode = preferences.node("responseHandler");
			try {
				ResponseHandler handler = responseHandlerDesc.createResponseHandler();
				if (handler instanceof Persistable) {
					((Persistable)handler).save(handlerNode);
				}
			} catch (CoreException e) {
			}
		}
		params.save(preferences);
	}

	public Results transfer(IProgressMonitor monitor, Copyable... copyables) {
		HttpProtocol protocol = new HttpProtocol();
		return protocol.transfer(monitor, this, copyables);
	}

	public ResponseHandler getResponseHandler() throws CoreException {
		return responseHandlerDesc.createResponseHandler();
	}

	public Collection<TargetParam<?>> getParams() {
		return params.getParams();
	}

	public void setParams(Collection<TargetParam<?>> params) {
		this.params.setParams(params);
	}
}

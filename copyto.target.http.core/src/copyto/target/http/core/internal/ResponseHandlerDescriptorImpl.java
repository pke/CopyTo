package copyto.target.http.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import copyto.target.http.core.ResponseHandler;
import copyto.target.http.core.ResponseHandlerDescriptor;
import eclipseutils.core.extensions.BaseExtensionDescriptor;

public class ResponseHandlerDescriptorImpl extends BaseExtensionDescriptor implements ResponseHandlerDescriptor {
	private ResponseHandler handler;

	public ResponseHandlerDescriptorImpl(IConfigurationElement configElement) {
		super(configElement);
	}

	public ResponseHandler createResponseHandler() throws CoreException {
		if (handler == null) {
			handler = createExecutableExtension();
		}
		return handler;
	}
}
package copyto.protocol.http.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import copyto.protocol.http.core.ResponseHandler;
import copyto.protocol.http.core.ResponseHandlerDescriptor;

public class ResponseHandlerDescriptorImpl implements ResponseHandlerDescriptor {
	private final IConfigurationElement configElement;
	private ResponseHandler handler;

	public ResponseHandlerDescriptorImpl(IConfigurationElement configElement) {
		this.configElement = configElement;
	}

	public ResponseHandler createResponseHandler() throws CoreException {
		if (handler == null) {
			handler = (ResponseHandler) configElement
					.createExecutableExtension("class");
		}
		return handler;
	}

	public String getId() {
		return configElement.getAttribute("id");
	}

	public String getName() {
		return configElement.getAttribute("name");
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ResponseHandlerDescriptor
				&& getId().equals(((ResponseHandlerDescriptor) obj).getId());
	}

	@Override
	public String toString() {
		return getName();
	}
}
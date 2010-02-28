package copyto.target.http.core.internal;

import org.eclipse.core.runtime.IConfigurationElement;

import copyto.target.http.core.ResponseHandlerDescriptor;
import eclipseutils.core.extensions.ExtensionPoint;

public final class ResponseHandlers extends ExtensionPoint<ResponseHandlerDescriptor> {

	private static final String EP_NAME = "copyto.protocol.http.core.responseHandlers";
	private static final String[] REQ_ATTR = { "id", "name", "class" };
	
	private static ResponseHandlers instance;
	
	public static ResponseHandlers getInstance() {
		if (instance == null) {
			instance = new ResponseHandlers();
		}
		return instance;
	}

	private ResponseHandlers() {
		super(EP_NAME, REQ_ATTR);
	}

	@Override
	protected ResponseHandlerDescriptor create(
			IConfigurationElement configElement) {
		return new ResponseHandlerDescriptorImpl(configElement);
	}
}

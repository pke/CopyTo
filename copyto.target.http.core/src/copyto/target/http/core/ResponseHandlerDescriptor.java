package copyto.target.http.core;

import org.eclipse.core.runtime.CoreException;

public interface ResponseHandlerDescriptor {
	String getId();
	
	String getName();
	
	ResponseHandler createResponseHandler() throws CoreException;
}

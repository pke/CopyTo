package copyto.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;

public abstract class ExtensionTargetFactory implements IExecutableExtension,
		TargetFactory {

	private IConfigurationElement config;

	public String getId() {
		return config.getAttribute("id");
	}

	public String getName() {
		return config.getAttribute("name");
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		this.config = config;
	}
}

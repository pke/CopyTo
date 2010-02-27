package eclipseutils.core.extensions;

import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;

public abstract class EP<T> {
	private final String id;
	private final ExtensionVisitor<T> visitor;
	
	public EP(String id, String... requiredAttribs) {
		this.id = id;
		visitor = new ExtensionVisitor<T>(requiredAttribs) {
			public T create(IConfigurationElement configElement) {
				return EP.this.create(configElement);
			}
		};
	}

	protected abstract T create(IConfigurationElement configElement);

	public T find(String id) {
		return ExtensionPoints.find(this.id, "id", id, visitor);
	}

	public Collection<T> findAll() {
		return ExtensionPoints.visitAll(id, visitor);
	}
}

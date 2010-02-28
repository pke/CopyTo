package eclipseutils.core.extensions;

import java.util.Collection;

import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.IConfigurationElement;

public abstract class ExtensionPoint<T> {
	private final String id;
	private final ExtensionVisitor<T> visitor;
	private String elementId = "id";

	public ExtensionPoint(String id, String... requiredAttribs) {
		this.id = id;
		visitor = new ExtensionVisitor<T>(requiredAttribs) {
			public T create(IConfigurationElement configElement) {
				return ExtensionPoint.this.create(configElement);
			}
		};
	}

	protected abstract T create(IConfigurationElement configElement);

	public T find(String id) {
		return ExtensionPoints.find(this.id, elementId, id, visitor);
	}

	public void setElementId(String id) {
		elementId = id;
	}
	
	public Collection<T> findAll() {
		return ExtensionPoints.visitAll(id, visitor);
	}

	public Collection<T> findAll(IEvaluationContext context,
			String enablementName) {
		return ExtensionPoints.visitAll(id, new ExpressionEvaluatingVisitor<T>(
				visitor, context, enablementName));
	}

	public Collection<T> findAll(IEvaluationContext context) {
		return findAll(context, ExpressionTagNames.ENABLEMENT);
	}
}

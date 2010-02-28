package eclipseutils.core.extensions;

import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import eclipseutils.core.extensions.internal.Visitor;
import eclipseutils.core.extensions.internal.Visitors;

public final class ExtensionPoints {

	public static <R> R visit(String extensionPointId,
			Visitor<IConfigurationElement, R> visitor) {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointId);
		return Visitors.visit(configurationElements, visitor);
	}

	public static <R> Collection<R> visitAll(String extensionPointId,
			Visitor<IConfigurationElement, R> visitor) {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointId);
		return Visitors.visitAllUnique(configurationElements, visitor);
	}

	public static <R> R find(String extensionPointId, final String attribute,
			final String value, final ExtensionVisitor<R> visitor) {
		return visit(extensionPointId, new ExtensionVisitor<R>(attribute) {

			@Override
			protected R create(IConfigurationElement configElement) {
				if (value.equals(configElement.getAttribute(attribute))) {
					return visitor.visit(configElement);
				}
				return null;
			}
		});
	}

	private ExtensionPoints() {
	}
}

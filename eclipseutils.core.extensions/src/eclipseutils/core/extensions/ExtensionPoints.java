package eclipseutils.core.extensions;

import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public final class ExtensionPoints {

	public static <R> R visit(String extensionPointId,
			Visitor<IConfigurationElement, R> visitor) {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointId);
		return Visitors.visit(configurationElements, visitor);
	}

	private ExtensionPoints() {
	}

	public static <R> Collection<R> visitAll(String extensionPointId,
			Visitor<IConfigurationElement, R> visitor) {
		IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPointId);
		return Visitors.visitAll(configurationElements, visitor);
	}

	public static <R> R find(String extensionPointId, final String attribute,
			final String value, final Visitor<IConfigurationElement, R> visitor) {
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
}

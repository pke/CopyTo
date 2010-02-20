package eclipseutils.core.extensions;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Allows to inspect a given {@link IConfigurationElement}.
 * 
 * <p>
 * This is used together with {@link ExtensionPoint#getElements(org.eclipse.core.expressions.IEvaluationContext, boolean, ExtensionPointFilter)}.
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public interface ExtensionPointVisitor {

	/**
	 * @param element
	 * @return whether or not to the given <code>element</code> was accepted. That can be evaluated by the caller.
	 */
	boolean accept(IConfigurationElement element);
}

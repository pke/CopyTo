package eclipseutils.core.extensions;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides easy access to configuration elements for a certain ExtensionPoint.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class ExtensionPoint {
	private final String extensionPointId;
	private final String enablementName;

	public ExtensionPoint(final Bundle bundle, final String extensionPointName, final String enablementName) {
		this(bundle.getSymbolicName() + '.' + extensionPointName, enablementName);
	}

	public ExtensionPoint(final Bundle bundle, final String extensionPointName) {
		this(bundle.getSymbolicName() + '.' + extensionPointName, ExpressionTagNames.ENABLEMENT);
	}

	public ExtensionPoint(final String extensionPointId) {
		this(extensionPointId, ExpressionTagNames.ENABLEMENT);
	}

	public ExtensionPoint(final String extensionPointId, final String enablementName) {
		Assert.isLegal(extensionPointId != null && extensionPointId.length() > 0);
		Assert.isLegal(enablementName != null && enablementName.length() > 0);
		this.extensionPointId = extensionPointId;
		this.enablementName = enablementName;
	}

	private boolean matches(final IConfigurationElement element, final IEvaluationContext context,
			final boolean enablementRequired) throws CoreException {
		if (null == context) {
			return true;
		}
		final IConfigurationElement[] elements = element.getChildren(this.enablementName);

		if (elements.length == 0) {
			return enablementRequired ? false : true;
		}
		Assert.isTrue(elements.length == 1);
		final Expression exp = ExpressionConverter.getDefault().perform(elements[0]);
		if (EvaluationResult.FALSE == exp.evaluate(context)) {
			return false;
		}
		// EvaluationResult.NOT_LOADED means a match too
		return true;
	}

	/**
	 * Gets all extensions that are enabled for the given context.
	 * 
	 * @param context to use for evaluation
	 * @param enablementRequired whether all extensions MUST specify an enablement.
	 * @return
	 */
	public Collection<IConfigurationElement> getElements(final IEvaluationContext context,
			final boolean enablementRequired) {
		return getElements(context, enablementRequired, null);
	}

	/**
	 * Finds all currently registered extensions for this extension-point.
	 * 
	 * <p>
	 * For each extension the filter is called
	 * 
	 * @param context
	 * @param enablementRequired
	 * @param visitor
	 * @return
	 */
	public IStatus visitElements(final IEvaluationContext context, final boolean enablementRequired,
			final ExtensionPointVisitor visitor) {
		Assert.isLegal(visitor != null);

		final MultiStatus multiStatus = new MultiStatus(FrameworkUtil.getBundle(visitor.getClass()).getSymbolicName(), 0, null,
				null);

		final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(this.extensionPointId);

		for (final IConfigurationElement element : configurationElements) {
			try {
				if (matches(element, context, enablementRequired)) {
					visitor.accept(element);
				}
			} catch (final Exception e) {
				/*filterStatus.add(StatusHelper.error(this, e,
						"Could not evaluate extension {0} in {1}", element.getName(), element.getContributor() //$NON-NLS-1$
								.getName()));*/
			}
		}

		return multiStatus;
	}

	/**
	 * Gets all extensions that are enabled for the given context and allows their filtering.
	 * 
	 * @param context the context to evaluate the extensions against
	 * @param enablementRequired if all extensions MUST specify an enablement
	 * @param visitor allows to filter the matching extensions. Can be <code>null</code> to add all matching extensions
	 * to the returned collection.
	 * @return the matched extensions that have passed the filter.
	 */
	public Collection<IConfigurationElement> getElements(final IEvaluationContext context,
			final boolean enablementRequired, final ExtensionPointVisitor visitor) {
		final ArrayList<IConfigurationElement> results = new ArrayList<IConfigurationElement>();

		visitElements(context, enablementRequired, new ExtensionPointVisitor() {
			public boolean accept(final IConfigurationElement element) {
				if (visitor == null || visitor.accept(element)) {
					results.add(element);
				}
				return true;
			}
		});
		return results;
	}
}

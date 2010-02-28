package eclipseutils.core.extensions;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;

import eclipseutils.core.extensions.internal.Visitor;

public final class ExpressionEvaluatingVisitor<T> implements
		Visitor<IConfigurationElement, T> {

	private final IEvaluationContext context;
	private final String enablementName;
	private final Visitor<IConfigurationElement, T> delegated;
	private final boolean enablementRequired;

	public ExpressionEvaluatingVisitor(
			Visitor<IConfigurationElement, T> delegated,
			IEvaluationContext context, String enablementName,
			boolean enablementRequired) {
		this.context = context;
		this.enablementName = enablementName;
		this.delegated = delegated;
		this.enablementRequired = enablementRequired;
	}

	public ExpressionEvaluatingVisitor(
			Visitor<IConfigurationElement, T> delegated,
			IEvaluationContext context, String enablementName) {
		this(delegated, context, enablementName, true);
	}

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			Visitor<IConfigurationElement, T> delegated,
			boolean enablementRequired) {
		this(delegated, context, ExpressionTagNames.ENABLEMENT,
				enablementRequired);
	}

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			Visitor<IConfigurationElement, T> delegated) {
		this(delegated, context, ExpressionTagNames.ENABLEMENT, true);
	}

	public T visit(IConfigurationElement config) {
		final IConfigurationElement[] elements = config
				.getChildren(this.enablementName);

		if (elements.length == 0) {
			return enablementRequired ? null : delegated.visit(config);
		}
		try {
			Assert.isTrue(elements.length == 1);
			Expression exp = ExpressionConverter.getDefault().perform(
					elements[0]);
			if (EvaluationResult.FALSE == exp.evaluate(context)) {
				return null;
			}
			// EvaluationResult.NOT_LOADED means a match too
			return delegated.visit(config);
		} catch (Exception e) {
			return null;
		}
	}
}
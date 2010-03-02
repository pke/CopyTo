package eclipseutils.core.extensions;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;

import patterns.visitor.Visitor;

public final class ExpressionEvaluatingVisitor<T> implements
		Visitor<IConfigurationElement, T> {

	private final IEvaluationContext context;
	private final String enablementName;
	private final Visitor<IConfigurationElement, T> delegated;
	private final boolean enablementRequired;

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			String enablementName, boolean enablementRequired,
			Visitor<IConfigurationElement, T> delegated) {
		this.context = context;
		this.enablementName = enablementName;
		this.delegated = delegated;
		this.enablementRequired = enablementRequired;
	}

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			String enablementName, Visitor<IConfigurationElement, T> delegated) {
		this(context, enablementName, true, delegated);
	}

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			boolean enablementRequired,
			Visitor<IConfigurationElement, T> delegated) {
		this(context, ExpressionTagNames.ENABLEMENT,
				enablementRequired, delegated);
	}

	public ExpressionEvaluatingVisitor(IEvaluationContext context,
			Visitor<IConfigurationElement, T> delegated) {
		this(context, ExpressionTagNames.ENABLEMENT, true, delegated);
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
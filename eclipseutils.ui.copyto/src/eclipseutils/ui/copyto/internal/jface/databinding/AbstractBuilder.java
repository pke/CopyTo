package eclipseutils.ui.copyto.internal.jface.databinding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.swt.widgets.Composite;

import eclipseutils.ui.copyto.internal.preferences.ControlCreator;

public abstract class AbstractBuilder implements Builder {
	static final Map<Object, ControlCreator> creators = new HashMap<Object, ControlCreator>();
	static {
		creators.put(String.class, new TextEditorControlCreator());
		creators.put(Boolean.class, BooleanControlCreator.getInstance());
		creators.put(boolean.class, BooleanControlCreator.getInstance());
	}

	private final Composite parent;
	private final Object bean;
	private final int targetToModelPolicy;

	public AbstractBuilder(final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		this.parent = parent;
		this.bean = bean;
		this.targetToModelPolicy = targetToModelPolicy;
	}

	public Builder field(final String property,
			final UpdateValueStrategy targetToModel) {
		final IObservableValue beanValueProperty = BeansObservables
				.observeValue(bean, property);
		final Object type = beanValueProperty.getValueType();
		final ControlCreator creator = creators.get(type);
		if (creator != null) {
			final IObservableValue control = creator.createControl(parent,
					bean, property);
			if (control != null) {
				bindValue(control, beanValueProperty, targetToModel);
			}
		}
		return this;
	}

	public Builder field(final String property,
			final IValidator afterConvertValidator) {
		return field(property, new UpdateValueStrategy(targetToModelPolicy)
				.setAfterConvertValidator(afterConvertValidator));
	}

	public Builder field(final String property) {
		return field(property, (IValidator) null);
	}

	public Builder newLine() {
		return this;
	}

	protected Composite getParent() {
		return parent;
	}

	protected Object getBean() {
		return bean;
	}

	protected abstract void bindValue(final IObservableValue target,
			final IObservableValue model, UpdateValueStrategy targetToModel);
}

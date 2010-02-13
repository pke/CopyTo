/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package eclipseutils.jface.databinding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.databinding.dialog.TitleAreaDialogSupport;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.internal.databinding.provisional.fieldassist.ControlDecorationSupport;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;



/**
 * An abstract implementation of the <code>Builder</code> interface.
 * 
 * <p>
 * Subclasses must provide layout methods.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
@SuppressWarnings("restriction")
public abstract class AbstractBuilder implements Builder {
	static final Map<Object, EditorCreator> creators = new HashMap<Object, EditorCreator>();
	static {
		creators.put(String.class, new TextEditorCreator());
		creators.put(Boolean.class, BooleanEditorCreator.getInstance());
		creators.put(boolean.class, BooleanEditorCreator.getInstance());
	}

	private final Composite parent;
	private final Object bean;
	private final int targetToModelPolicy;
	private final ControlCreator toolkit;
	private int fields;
	protected final DataBindingContext ctx;

	public AbstractBuilder(final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		this(SWTControlCreator.getInstance(), parent, bean, targetToModelPolicy);
	}

	public AbstractBuilder(final ControlCreator toolkit,
			final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		ctx = new DataBindingContext();
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				ctx.dispose();
			}
		});
		this.parent = parent;
		this.bean = bean;
		this.targetToModelPolicy = targetToModelPolicy;
		this.toolkit = toolkit;
	}

	public Builder field(final String property, final FieldOptions fieldOptions) {
		final IObservableValue beanValueProperty = BeansObservables
				.observeValue(bean, property);
		if (beanValueProperty == null) {
			return this;
		}
		final Object type = beanValueProperty.getValueType();
		final EditorCreator creator = creators.get(type);
		if (creator != null) {
			final Label label = creator.hasLabel() ? toolkit.createLabel(
					parent, LocalizationHelper.getLabel(bean, property) + ":",
					SWT.LEFT) : null;
			if (label != null) {
				AbstractEditorCreator.setToolTip(label, bean, property);
			}
			final IObservableValue control = creator.create(toolkit, parent,
					bean, property);
			if (control != null) {
				++fields;
				UpdateValueStrategy targetToModel = fieldOptions != null ? fieldOptions
						.getTargetToModel()
						: null;
				if (targetToModel == null) {
					targetToModel = new UpdateValueStrategy(targetToModelPolicy);
					// Set also any validator
					if (fieldOptions != null) {
						targetToModel.setAfterConvertValidator(fieldOptions
								.getValidator());
					}
				}
				bind(control, beanValueProperty, targetToModel);
				final Control controlWidget = getControl(control);
				if (controlWidget != null && fieldOptions != null) {
					addContentProposal(controlWidget, fieldOptions);
				}
				applyLayout(label, controlWidget);
			} else {
				label.dispose();
			}
		}
		return this;
	}

	protected abstract void applyLayout(Label label, Control controlWidget);

	private Control getControl(final IObservableValue observableValue) {
		if (observableValue instanceof ISWTObservable) {
			final Widget widget = ((ISWTObservable) observableValue)
					.getWidget();
			if (widget instanceof Control) {
				return (Control) widget;
			}
		}
		return null;
	}

	private void addContentProposal(final Control control,
			final FieldOptions fieldOptions) {
		if (!(control instanceof Text)) {
			return;
		}
		final IContentProposalProvider proposalProvider = fieldOptions
				.getProposalProvider();
		if (proposalProvider != null) {
			final Text text = (Text) control;
			final KeyStroke proposalKeyStroke = fieldOptions
					.getProposalKeyStroke();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(
					text, new TextContentAdapter(), proposalProvider,
					proposalKeyStroke, fieldOptions
							.getAutoActivationnCharacters());
			adapter.setPropagateKeys(true);
			adapter.setLabelProvider(fieldOptions.getProposalLabelProvider());
			adapter.setProposalAcceptanceStyle(fieldOptions
					.getProposalAcceptanceStyle());
			final FieldDecoration decoration = FieldDecorationRegistry
					.getDefault().getFieldDecoration(
							FieldDecorationRegistry.DEC_CONTENT_PROPOSAL);
			final ControlDecoration dec = new ControlDecoration(text,
					SWT.BOTTOM | SWT.LEFT);
			dec.setImage(decoration.getImage());
			dec.setShowOnlyOnFocus(true);
			dec.setDescriptionText(NLS.bind(fieldOptions
					.getProposalDescription(), proposalKeyStroke));
		}
	}

	public Builder field(final String property) {
		return field(property, null);
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

	protected void bind(final IObservableValue target,
			final IObservableValue model,
			final UpdateValueStrategy targetToModel) {
		final Binding binding = ctx.bindValue(target, model, targetToModel,
				null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.RIGHT);
	}

	public Builder updateModels() {
		ctx.updateModels();
		return this;
	}

	public Builder addDialogSupport(final TitleAreaDialog dialog,
			final IObservableValue target) {
		final AggregateValidationStatus status = new AggregateValidationStatus(
				ctx, AggregateValidationStatus.MAX_SEVERITY);
		if (target != null) {
			status.addChangeListener(new IChangeListener() {
				public void handleChange(final ChangeEvent event) {
					final Object value = status.getValue();
					if (value instanceof IStatus) {
						target.setValue(value);
					}
				}
			});
		}
		TitleAreaDialogSupport.create(dialog, ctx);
		return this;
	}
}

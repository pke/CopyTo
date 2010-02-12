package eclipseutils.ui.copyto.internal.jface.databinding;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.databinding.swt.ISWTObservable;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

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
	private final CreatorToolkit toolkit;

	public AbstractBuilder(final CreatorToolkit toolkit,
			final Composite parent, final Object bean,
			final int targetToModelPolicy) {
		this.parent = parent;
		this.bean = bean;
		this.targetToModelPolicy = targetToModelPolicy;
		this.toolkit = toolkit;
	}

	static class SWTToolkit implements CreatorToolkit {
		private static SWTToolkit instance;

		public static CreatorToolkit getInstance() {
			if (instance == null) {
				instance = new SWTToolkit();
			}
			return instance;
		}

		public Button createButton(final Composite parent, final String text,
				final int style) {
			final Button control = new Button(parent, style);
			return setText(control, text);
		}

		protected static <T extends Control> T setText(final T control,
				final String text) {
			if (text != null) {
				if (control instanceof Button) {
					((Button) control).setText(text);
				} else if (control instanceof Label) {
					((Label) control).setText(text);
				}
			}
			return control;
		}

		public Text createText(final Composite parent, final String text,
				final int style) {
			final Text control = new Text(parent, style);
			return setText(control, text);
		}

		public Label createLabel(final Composite parent, final String text,
				final int style) {
			final Label control = new Label(parent, style);
			return setText(control, text);
		}
	}

	public Builder field(final String property, final FieldOptions fieldOptions) {
		final IObservableValue beanValueProperty = BeansObservables
				.observeValue(bean, property);
		final Object type = beanValueProperty.getValueType();
		final ControlCreator creator = creators.get(type);
		if (creator != null) {
			final IObservableValue control = creator.create(toolkit, parent,
					bean, property);
			if (control != null) {
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
				bindValue(control, beanValueProperty, targetToModel);
				if (fieldOptions != null) {
					addContentProposal(control, fieldOptions);
				}
			}
		}
		return this;
	}

	private void addContentProposal(final IObservableValue control,
			final FieldOptions fieldOptions) {
		final IContentProposalProvider proposalProvider = fieldOptions
				.getProposalProvider();
		if (proposalProvider != null && control instanceof ISWTObservable) {
			final ISWTObservable observable = (ISWTObservable) control;
			if (!(observable.getWidget() instanceof Text)) {
				return;
			}
			final Text text = (Text) observable.getWidget();
			final KeyStroke proposalKeyStroke = fieldOptions
					.getProposalKeyStroke();
			final ContentProposalAdapter adapter = new ContentProposalAdapter(
					text, new TextContentAdapter(), proposalProvider,
					proposalKeyStroke, fieldOptions
							.getAutoActivationnCharacters());
			adapter.setPropagateKeys(true);
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

	protected abstract void bindValue(final IObservableValue target,
			final IObservableValue model, UpdateValueStrategy targetToModel);
}

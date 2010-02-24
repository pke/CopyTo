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
package copyto.ui.internal.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import copyto.core.Target;
import copyto.core.internal.html.form.Form;
import copyto.core.internal.html.form.FormParser;
import copyto.core.internal.html.form.HtmlElement;
import copyto.core.internal.html.form.TextAreaElement;
import copyto.ui.internal.Messages;
import eclipseutils.jface.databinding.Builder;
import eclipseutils.jface.databinding.BuiltTitleAreaDialog;
import eclipseutils.jface.databinding.FieldOptions;
import eclipseutils.jface.databinding.FieldOptions.ControlCustomizer;
import eclipseutils.jface.databinding.GridLayoutBuilder;
import eclipseutils.jface.databinding.customizers.SelectAllOnFocus;
import eclipseutils.jface.databinding.validators.AbstractValidator;
import eclipseutils.jface.databinding.validators.CompoundValidator;
import eclipseutils.jface.databinding.validators.ListValidator;
import eclipseutils.jface.databinding.validators.NotEmptyValidator;
import eclipseutils.jface.databinding.validators.NotValidator;
import eclipseutils.jface.databinding.validators.URLValidator;

/**
 * TODO: Add validation of URL that it contains at least ${copyto.text} Make
 * sure the preferences are initialized.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
class EditDialog extends BuiltTitleAreaDialog {

	private static final class TextVariableValidator extends AbstractValidator {

		private static TextVariableValidator instance;

		private TextVariableValidator() {
			super(IStatus.ERROR);
		}

		@Override
		protected String performValidation(final Object value) throws Throwable {
			final String string = (String) value;
			final int firstIndex = string.indexOf(COPYTO_TEXT_VAR);
			if (firstIndex == -1) {
				return Messages.EditDialog_URL_MustContainTextVar;
			}
			if (string.lastIndexOf(COPYTO_TEXT_VAR) != firstIndex) {
				return NLS.bind(Messages.EditDialog_URL_MustContainVarOnce,
						COPYTO_TEXT_VAR);
			}
			return null;
		}

		public static IValidator getInstance() {
			if (instance == null) {
				instance = new TextVariableValidator();
			}

			return instance;
		}
	}

	private static final String COPYTO_TEXT_VAR = "${copyto.text}"; //$NON-NLS-1$
	private static final String[] PROPOSALS = new String[] { COPYTO_TEXT_VAR,
			"${copyto.mime-type}", "${copyto.source}" }; //$NON-NLS-1$//$NON-NLS-2$
	private final Target target;
	private final HashSet<String> existingItems;
	private static final IValidator urlValidator = new CompoundValidator(
			URLValidator.getInstance(), TextVariableValidator.getInstance());

	public EditDialog(final Shell parentShell, final Target target,
			final Collection<Target> existingItems) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		this.target = target;
		this.existingItems = new HashSet<String>();
		for (final Target t : existingItems) {
			if (!t.getName().equals(target.getName())) {
				this.existingItems.add(t.getName());
			}
		}
		setHelpAvailable(false);
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.EditDialog_ShellText);
		final URL iconEntry = FileLocator.find(FrameworkUtil
				.getBundle(getClass()),
				new Path("$nl$/icons/e32/copyto.png"), null); //$NON-NLS-1$
		final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
				.createFromURL(iconEntry) : null;
		if (icon != null) {
			final Image image = icon.createImage();
			setTitleImage(image);
			newShell.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent e) {
					image.dispose();
				}
			});
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		autoDetectButton = new Button(parent, SWT.PUSH);
		autoDetectButton.setText("Auto Detect...");		
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Control control = super.createDialogArea(parent);
		setTitle(Messages.EditDialog_Title);
		setMessage(Messages.EditDialog_Desc);
		return control;
	}
	
	private class URLControlCustomizer implements ControlCustomizer {

		public void customizeControl(Control control,
				final IObservableValue observableValue) {
			
			autoDetectButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						URL url = new URL(observableValue.getValue().toString());
						FormParser parser = new FormParser();
						Collection<Form> forms = parser.parse(url);
						for (Form form : forms) {
							for (HtmlElement element : form.getElements()) {
								if (element instanceof TextAreaElement) {
									// This is our form!
								}
							}
						}
					} catch (MalformedURLException e) {
					}
				};
			});
		}
	}

	final static SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(
			PROPOSALS);
	private Button autoDetectButton;

	public Builder createBuilder(final Composite parent) {
		final IValidator labelValidator = new CompoundValidator(
				NotEmptyValidator.getInstance(true), new NotValidator(
						IStatus.WARNING, new ListValidator(existingItems),
						Messages.EditDialog_DuplicateTarget));
		/*
		final List<IStringVariable> vars = Arrays.asList(VariablesPlugin
				.getDefault().getStringVariableManager().getVariables());
		final String varNames[] = new String[vars.size() + PROPOSALS.length];
		for (int i = 0; i < vars.size(); ++i) {
			varNames[i] = vars.get(i).getName();
		}
		for (int i = 0; i < PROPOSALS.length; ++i) {
			varNames[vars.size() + i] = PROPOSALS[i];
		}
		
		proposalProvider.setProposals(varNames);
		*/

		return new GridLayoutBuilder(parent, target,
				UpdateValueStrategy.POLICY_CONVERT).field(
				"name", //$NON-NLS-1$
				new FieldOptions(labelValidator)
						.setControlCustomizer(new SelectAllOnFocus())).field(
				"url", //$NON-NLS-1$
				new FieldOptions(urlValidator).setProposalProvider(
						proposalProvider).setAutoActivationCharacters('$').setControlCustomizer(new URLControlCustomizer()));
	}
}
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
package eclipseutils.ui.copyto.internal.preferences;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.UpdateValueStrategy;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.internal.Target;
import eclipseutils.ui.copyto.internal.jface.databinding.Builder;
import eclipseutils.ui.copyto.internal.jface.databinding.BuiltTitleAreaDialog;
import eclipseutils.ui.copyto.internal.jface.databinding.FieldOptions;
import eclipseutils.ui.copyto.internal.jface.databinding.GridLayoutBuilder;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.AbstractValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.CompoundValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.ListValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.NotEmptyValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.NotValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.URLValidator;

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
				return "The URL must contain the \"${copyto.text}\" variable";
			}
			if (string.lastIndexOf(COPYTO_TEXT_VAR) != firstIndex) {
				return NLS.bind(
						"The URL contains the {0} variable more than once",
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

	private static final String COPYTO_TEXT_VAR = "${copyto.text}";
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
	protected Control createDialogArea(final Composite parent) {
		final Control control = super.createDialogArea(parent);
		getShell().setText("Copy To Target");
		setTitle("Target Definition");
		setMessage("Enter the definition for this CopyTo Target below");
		final URL iconEntry = FileLocator.find(FrameworkUtil
				.getBundle(getClass()),
				new Path("$nl$/icons/e32/copyto.png"), null); //$NON-NLS-1$
		final ImageDescriptor icon = (iconEntry != null) ? ImageDescriptor
				.createFromURL(iconEntry) : null;
		if (icon != null) {
			final Image image = icon.createImage();
			setTitleImage(image);
			parent.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent e) {
					image.dispose();
				}
			});
		}

		return control;
	}

	final static SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(
			new String[] { COPYTO_TEXT_VAR, "${copyto.mime-type}",
					"${copyto.source}" });

	public Builder createBuilder(final Composite parent) {
		final IValidator labelValidator = new CompoundValidator(
				NotEmptyValidator.getInstance(true),
				new NotValidator(
						new ListValidator(existingItems),
						"A Target with that name already exists. It's recommended to choose another name.",
						IStatus.WARNING));

		return new GridLayoutBuilder(parent, target,
				UpdateValueStrategy.POLICY_CONVERT).field("name",
				new FieldOptions(labelValidator)).field(
				"url",
				new FieldOptions(urlValidator).setProposalProvider(
						proposalProvider).setAutoActivationCharacters('$'));
	}
}
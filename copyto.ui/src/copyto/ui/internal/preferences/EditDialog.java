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

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

import copyto.core.Target;
import copyto.ui.internal.Messages;
import eclipseutils.jface.databinding.Builder;
import eclipseutils.jface.databinding.BuilderAdapter;
import eclipseutils.jface.databinding.BuiltTitleAreaDialog;
import eclipseutils.jface.databinding.FieldOptions;
import eclipseutils.jface.databinding.GridLayoutBuilder;
import eclipseutils.jface.databinding.customizers.SelectAllOnFocus;
import eclipseutils.jface.databinding.validators.CompoundValidator;
import eclipseutils.jface.databinding.validators.ListValidator;
import eclipseutils.jface.databinding.validators.NotEmptyValidator;
import eclipseutils.jface.databinding.validators.NotValidator;

/**
 * TODO: Add validation of URL that it contains at least ${copyto.text} Make
 * sure the preferences are initialized.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
class EditDialog extends BuiltTitleAreaDialog {
	private static final String COPYTO_TEXT_VAR = "${copyto.text}"; //$NON-NLS-1$
	private static final String[] PROPOSALS = new String[] { COPYTO_TEXT_VAR,
			"${copyto.mime-type}", "${copyto.source}" }; //$NON-NLS-1$//$NON-NLS-2$
	private final Target target;
	private final HashSet<String> existingItems;

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
		String name = target.getFactory().getName();
		
		newShell.setText(NLS.bind(Messages.EditDialog_ShellText, name));
		setTitle(NLS.bind(Messages.EditDialog_Title, name));
		//setMessage(NLS.bind(Messages.EditDialog_Desc, name));
		
		ImageDescriptor desc = (ImageDescriptor) Platform.getAdapterManager()
				.loadAdapter(target, ImageDescriptor.class.getName());

		if (desc == null) {
			final URL iconEntry = FileLocator.find(FrameworkUtil
					.getBundle(getClass()), new Path(
					"$nl$/icons/e32/copyto.png"), null); //$NON-NLS-1$
			desc = (iconEntry != null) ? ImageDescriptor
					.createFromURL(iconEntry) : null;
		}
		if (desc != null) {
			final Image image = desc.createImage();
			setTitleImage(image);
			newShell.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent e) {
					image.dispose();
				}
			});
		}
	}

	final static SimpleContentProposalProvider proposalProvider = new SimpleContentProposalProvider(
			PROPOSALS);

	public Builder createBuilder(final Composite parent) {
		final IValidator labelValidator = new CompoundValidator(
				NotEmptyValidator.getInstance(true), new NotValidator(
						IStatus.WARNING, new ListValidator(existingItems),
						Messages.EditDialog_DuplicateTarget));

		BuilderAdapter adapter = (BuilderAdapter) Platform.getAdapterManager()
				.loadAdapter(target, BuilderAdapter.class.getName());

		return adapter.create(target, new GridLayoutBuilder(parent, target,
				UpdateValueStrategy.POLICY_CONVERT).field("name", //$NON-NLS-1$
				new FieldOptions(labelValidator)
						.setControlCustomizer(new SelectAllOnFocus())));
	}
}
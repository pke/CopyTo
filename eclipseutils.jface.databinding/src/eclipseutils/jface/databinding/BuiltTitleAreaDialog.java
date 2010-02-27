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

import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Abstract subclass of <code>TitleAreaDialog</code> that creates its content
 * using a <code>Builder</code> provided by the implementor of this class.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class BuiltTitleAreaDialog extends TitleAreaDialog implements
		BuilderProvider {

	private Builder builder;
	private ControlDecoration okButtonDecoration;
	final FieldDecoration warningDecoration = FieldDecorationRegistry
			.getDefault().getFieldDecoration(
					FieldDecorationRegistry.DEC_WARNING);

	/**
	 * @param parentShell
	 */
	public BuiltTitleAreaDialog(final Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite client = new Composite((Composite) super
				.createDialogArea(parent), SWT.NULL);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(client);
		builder = createBuilder(client).addDialogSupport(this,
				new AbstractObservableValue() {
					public Object getValueType() {
						return null;
					}

					@Override
					protected Object doGetValue() {
						return null;
					}

					@Override
					protected void doSetValue(final Object value) {
						if (value instanceof IStatus) {
							final IStatus status = (IStatus) value;
							final Button button = getButton(IDialogConstants.OK_ID);
							if (button != null && !button.isDisposed()) {
								button.setEnabled(status.getSeverity() != IStatus.ERROR);
								if (status.getSeverity() == IStatus.WARNING) {
									okButtonDecoration.show();
								} else {
									okButtonDecoration.hide();
								}
							}
						}
					}
				});
		return client;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);

		Button okButton = getButton(IDialogConstants.OK_ID);
		okButtonDecoration = new ControlDecoration(okButton, SWT.TOP | SWT.LEFT);
		okButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				okButtonDecoration.dispose();
			}
		});
		okButtonDecoration.hide();
		okButtonDecoration.setMarginWidth(1);
		okButtonDecoration.setImage(warningDecoration.getImage());
		okButtonDecoration
				.setDescriptionText("The dialog contains warnings. Closing the dialog is discouraged.");

	}

	@Override
	protected void okPressed() {
		builder.updateModels();
		super.okPressed();
	}
}

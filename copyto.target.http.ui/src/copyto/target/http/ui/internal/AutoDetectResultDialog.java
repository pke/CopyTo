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
package copyto.target.http.ui.internal;

import java.net.URL;
import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.masterdetail.MasterDetailObservables;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import copyto.target.http.core.internal.html.form.Form;
import copyto.target.http.core.internal.html.form.TextAreaElement;

/**
 * Displays the results of an form auto-detect operation.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class AutoDetectResultDialog extends TitleAreaDialog {

	private final Collection<Form> forms;
	private Form selectedForm;
	private IViewerObservableValue selection;
	private URL url;

	public AutoDetectResultDialog(Shell shell, URL url, Collection<Form> forms) {
		super(shell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		setHelpAvailable(false);
		this.url = url;
		this.forms = forms;
	}

	@Override
	protected void okPressed() {
		selectedForm = (Form) selection.getValue();
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Multiple forms detected");
		setMessage(String.format("At %s where multiple forms found.", url));

		Composite client = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.swtDefaults().applyTo(client);
		Label label = new Label(client, SWT.LEFT);
		label.setText("Please select the form you want to use" + ":");
		TableViewer viewer = new TableViewer(client);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(
				viewer.getControl());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Form form = (Form) element;
				TextAreaElement textArea = form.findTextArea();
				return String.format("Form: %s %s", form.getName(),
						textArea != null ? "(suggestion)" : "");
			}
		});
		viewer.setInput(forms);
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				okPressed();
			}
		});

		selection = ViewersObservables.observeSingleSelection(viewer);

		Text text = new Text(client, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY
				| SWT.V_SCROLL);
		text.setFont(JFaceResources.getTextFont());
		GridDataFactory.fillDefaults().hint(SWT.DEFAULT, 200).grab(true, true)
				.applyTo(text);
		IObservableValue detail = MasterDetailObservables.detailValue(
				selection, new IObservableFactory() {
					public IObservable createObservable(final Object target) {
						return new AbstractObservableValue() {

							public Object getValueType() {
								return String.class;
							}

							@Override
							protected Object doGetValue() {
								return target.toString();
							}
						};
					}
				}, String.class);
		final DataBindingContext dbx = new DataBindingContext();
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dbx.dispose();
			}
		});
		dbx.bindValue(
				SWTObservables.observeText(text, SWT.None),
				detail,
				new UpdateValueStrategy(false, UpdateValueStrategy.POLICY_NEVER),
				null);

		for (Form form : forms) {
			TextAreaElement textArea = form.findTextArea();
			if (textArea != null) {
				selection.setValue(form);
				break;
			}
		}
		viewer.getControl().setFocus();
		return client;
	}

	public static Form select(Shell shell, URL url, Collection<Form> forms) {
		AutoDetectResultDialog dialog = new AutoDetectResultDialog(shell, url,
				forms);
		if (dialog.open() == Window.OK) {
			return (Form) dialog.selectedForm;
		}
		return null;
	}
}

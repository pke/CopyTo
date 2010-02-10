package eclipseutils.ui.copyto.internal.preferences;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.Preferences;

import eclipseutils.ui.copyto.internal.Target;

class TargetFieldEditor extends TableViewerFieldEditor<Target> {
	TargetFieldEditor(String preferencePath, String labelText, Composite parent) {
		super(preferencePath, labelText, parent);
	}

	@Override
	protected Target createItem(Preferences preferences) {
		if (preferences != null) {
			return new Target(preferences);
		}
		Target target = new Target();
		if (new EditDialog(getPage().getShell(), target).open() == Window.OK) {
			return target;
		}
		return null;
	}

	/**
	 * TODO: Add validation of URL that it contains at least ${copyto.text}
	 * 
	 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
	 * 
	 */
	class EditDialog extends TitleAreaDialog {

		private final Target target;
		private DataBindingContext ctx;

		public EditDialog(Shell parentShell, Target target) {
			super(parentShell);
			setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
					| SWT.APPLICATION_MODAL | SWT.RESIZE
					| getDefaultOrientation());
			this.target = target;
			setHelpAvailable(false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			getShell().setText("CopyTo Target");
			setTitle("Target informations");
			setMessage("Enter the informations for this CopyTo target below");
			final UpdateValueStrategy targetToModel = new UpdateValueStrategy(
					UpdateValueStrategy.POLICY_ON_REQUEST);
			ctx = new DataBindingContext();
			parent.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					ctx.dispose();
				}
			});
			Composite client = new Composite((Composite) super
					.createDialogArea(parent), SWT.NULL);
			GridLayoutFactory.swtDefaults().numColumns(2).applyTo(client);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(client);

			Label label = new Label(client, SWT.RIGHT);
			label.setText("Label" + ":");
			GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(
					label);
			Text text = new Text(client, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(text);

			ISWTObservableValue textObserver = SWTObservables.observeText(text);
			IObservableValue beanValueProperty = BeansObservables.observeValue(
					target, "label");
			ctx.bindValue(textObserver, beanValueProperty, targetToModel, null);

			label = new Label(client, SWT.RIGHT);
			label.setText("URL" + ":");
			GridDataFactory.swtDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(
					label);
			text = new Text(client, SWT.BORDER);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
			textObserver = SWTObservables.observeText(text);
			beanValueProperty = BeansObservables.observeValue(target, "url");
			ctx.bindValue(textObserver, beanValueProperty, targetToModel, null);

			return client;
		}

		@Override
		protected void okPressed() {
			ctx.updateModels();
			super.okPressed();
		}
	}

	@Override
	protected void createCustomButtons(Composite parent) {
		final Button editButton = createPushButton(parent, "Edit...");
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Target target = (Target) getViewerSelectionValue().getValue();
				new EditDialog(editButton.getShell(), target).open();
			}
		});
		enableWithSelection(editButton, SWT.SINGLE);

		final Button testButton = createPushButton(parent, "Test");
		testButton.setToolTipText("Test the connectivity to the selected URL");
		enableWithSelection(testButton, SWT.SINGLE | SWT.MULTI);
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				testButton.setEnabled(false);
				visitViewerSelection("Testing connection",
						new Visitor<Target>() {
							public void visit(final Target target,
									IProgressMonitor monitor) {
								monitor.beginTask(NLS.bind("Testing {0}: ",
										target.getLabel(), target.getUrl()),
										IProgressMonitor.UNKNOWN);
								for (int i = 0; i < 10; ++i) {
									target.testConnection();
								}
							}
						});
				testButton.setEnabled(true);
			}
		});
		Button copyButton = createPushButton(parent, "Copy");
		enableWithSelection(copyButton, SWT.SINGLE);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				visitViewerSelection("Copying...", new Visitor<Target>() {
					public void visit(Target item, IProgressMonitor monitor) {
						final String base64 = item.toBase64();
						Display.getDefault().syncExec(new Runnable() {

							public void run() {
								Clipboard clipboard = new Clipboard(Display
										.getDefault());
								try {
									clipboard.setContents(
											new Object[] { base64 },
											new Transfer[] { TextTransfer
													.getInstance() });
								} finally {
									clipboard.dispose();
								}
							}
						});
					}
				});
			}
		});

		final Button pasteButton = createPushButton(parent, "Paste");
		pasteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Clipboard clipboard = new Clipboard(Display.getDefault());
				try {
					String base64 = (String) clipboard.getContents(TextTransfer
							.getInstance());
					Target item = Target.valueOf(base64);
					if (item != null) {
						add(item);
					}
				} catch (Exception ex) {
					MessageDialog
							.openError(pasteButton.getShell(),
									"Error pasting CopyTo target",
									"The clipboard does not contain a valid CopyTo target for pasting");
					ex.printStackTrace();
				} finally {
					clipboard.dispose();
				}
			}
		});

		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Target target = (Target) getViewerSelectionValue().getValue();
				new EditDialog(editButton.getShell(), target).open();
			}
		});
	}

	@Override
	protected void store(Target item, Preferences node) {
		item.save(node);
	}

	@Override
	protected String getId(Target item) {
		return item.getId();
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { "label", "url" };
	}

	@Override
	protected String[] getColumnLabels() {
		return new String[] { "Name", "URL" };
	}

	@Override
	protected EditingSupport createEditingSupport(String name,
			TableViewer viewer, DataBindingContext context) {
		/*if ("label".equals(name)) {
			IValueProperty cellEditorControlText = CellEditorProperties
					.control().value(WidgetProperties.text());
			return ObservableValueEditingSupport.create(viewer, context,
					new TextCellEditor(viewer.getTable()),
					cellEditorControlText, BeanProperties.value(Target.class,
							"label"));
		}*/
		return super.createEditingSupport(name, viewer, context);
	}
}
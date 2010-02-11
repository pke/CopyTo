package eclipseutils.ui.copyto.internal.jface.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.prefs.Preferences;

/**
 * An abstract field editor that manages a master/detail table of input values.
 * 
 * <p>
 * The editor displays a table containing the values, buttons for adding and
 * removing values.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            Type of the items the editor displays.
 */
public abstract class AbstractTableViewerFieldEditor<T> extends FieldEditor {

	/** Don't create add/remove buttons */
	protected static final int READ_ONLY = 0x0001;
	/** Don't create the edit button. Used for Master/Detail view. */
	protected static final int NO_EDIT_BUTTON = 0x0002;

	private TableViewer viewer;
	private Composite buttonBox;
	private IObservableList items;
	private DataBindingContext ctx;
	private final int flags;
	private IObservableValue viewerSingleSelectionValue;

	protected static interface Visitor<T> {
		void visit(T item, IProgressMonitor monitor);
	}

	protected AbstractTableViewerFieldEditor(final String preferencePath,
			final String labelText, final Composite parent, final int flags) {
		super(preferencePath, labelText, parent);
		this.flags = flags;
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	protected abstract String[] getColumnNames();

	protected abstract String[] getColumnLabels();

	/**
	 * Creates a new item. Is called when the user clicks the add button.
	 * 
	 * @param shell
	 *            to create a dialog on.
	 * @return a newly created item, or <code>null</code> if a new item could
	 *         not be created.
	 * @uithread This method is called from the UI-Thread.
	 */
	protected abstract T createItem(Shell shell);

	protected abstract void store(T item, Preferences node);

	/**
	 * <p>
	 * Subclasses that do not use the Master/Details capabilities of this editor
	 * must implement this and provide the user with the necessary means to edit
	 * the given <i>item</i>.
	 * 
	 * <p>
	 * This standard implementation does nothing.
	 * 
	 * @param shell
	 *            to create the editing user interface dialog on.
	 * @param item
	 *            to edit
	 */
	protected void editItem(final Shell shell, final T item) {
	}

	/**
	 * @param parent
	 * @return the table
	 */
	public Table getTableControl(final Composite parent) {
		if (viewer == null) {
			final Composite client = new Composite(parent, SWT.NULL);
			// client.setLayout(new FillLayout(SWT.VERTICAL));
			final TableColumnLayout tableLayout = new TableColumnLayout();
			client.setLayout(tableLayout);
			viewer = new TableViewer(client, SWT.BORDER | SWT.MULTI
					| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

			// viewer.setContentProvider(ArrayContentProvider.getInstance());
			// viewer.setInput(items);
			// sash = new Sash(client, SWT.VERTICAL | SWT.SMOOTH);
			// detailClient = new Composite(client, SWT.BORDER);

			final Table table = viewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setFont(parent.getFont());
			ctx = new DataBindingContext();
			table.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent e) {
					ctx.dispose();
				}
			});
			final String[] columnNames = getColumnNames();
			final String[] columnLables = getColumnLabels();

			for (int i = 0; i < columnNames.length; ++i) {
				final TableViewerColumn viewerColumn = new TableViewerColumn(
						viewer, SWT.LEFT);
				viewerColumn.getColumn().setText(columnLables[i]);
				viewerColumn.setEditingSupport(createEditingSupport(
						columnNames[i], viewer, ctx));
				tableLayout.setColumnData(viewerColumn.getColumn(),
						new ColumnWeightData(i == columnNames.length - 1 ? 100
								: 30));
			}
			items = new WritableList();
			ViewerSupport.bind(viewer, items, BeanProperties
					.values(columnNames));
			table.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					viewer = null;
				}
			});
			getViewer().addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(final DoubleClickEvent event) {
					editSelection();
				}
			});
		} else {
			checkParent(viewer.getTable(), parent);
		}
		return viewer.getTable();
	}

	/**
	 * Subclasses may overwrite to add in-line editing support to the table.
	 * 
	 * @param viewer
	 * @param context
	 * @param name
	 * @return <code>null</code> in the default implementation.
	 */
	protected EditingSupport createEditingSupport(final String name,
			final TableViewer viewer, final DataBindingContext context) {
		return null;
	}

	@Override
	public void setEnabled(final boolean enabled, final Composite parent) {
		super.setEnabled(enabled, parent);

		getTableControl(parent).setEnabled(enabled);

		for (final Control control : this.buttonBox.getChildren()) {
			control.setEnabled(enabled);
		}
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) viewer.getControl().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		GridDataFactory.fillDefaults().span(numColumns, 1).applyTo(
				getLabelControl(parent));

		final Table table = getTableControl(parent);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.heightHint = 300;
		gd.grabExcessHorizontalSpace = true;
		table.getParent().setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(
				buttonBox);
	}

	protected Button createPushButton(final Composite parent, final String text) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setFont(parent.getFont());
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		final int widthHint = convertHorizontalDLUsToPixels(button,
				IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return button;
	}

	UpdateValueStrategy selectionToBooleanConverter() {
		final UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		modelToTarget.setConverter(new Converter(Object.class, boolean.class) {
			public Object convert(final Object fromObject) {
				return fromObject != null;
			}
		});
		return modelToTarget;
	}

	/**
	 * Creates the add button.
	 * 
	 * <p>
	 * This standard implementation calls {@link #createItem(Shell)} when the
	 * button is clicked and adds the returned item to the list.
	 * 
	 * Subclasses may override to provide their own add button, or prevent the
	 * creation of the button for read-only lists that can not have items added
	 * or removed.
	 * 
	 * @param parent
	 *            to create the button on
	 */
	protected void createAddButton(final Composite parent) {
		if (isReadOnly()) {
			return;
		}
		final Button button = createPushButton(parent, JFaceResources
				.getString("ListEditor.add"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final T item = createItem(getPage().getShell());
				if (item != null) {
					add(item);
				}
			}
		});
	}

	protected void createRemoveButton(final Composite parent) {
		if (isReadOnly()) {
			return;
		}
		final Button button = createPushButton(parent, JFaceResources
				.getString("ListEditor.remove"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				visitViewerSelection("Removing selected elements",
						new Visitor<T>() {
							public void visit(final T item,
									final IProgressMonitor monitor) {
								remove(item);
							}
						});
			}
		});
		enableWithSelection(button, SWT.MULTI | SWT.SINGLE);
	}

	protected void createEditButton(final Composite parent) {
		final Button editButton = createPushButton(parent, "Edit...");
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				editSelection();
			}
		});
		enableWithSelection(editButton, SWT.SINGLE);
	}

	@SuppressWarnings("unchecked")
	private void editSelection() {
		if ((flags & NO_EDIT_BUTTON) == NO_EDIT_BUTTON) {
			return;
		}
		final T item = (T) getViewerSelectionValue().getValue();
		if (item != null) {
			editItem(getPage().getShell(), item);
		}
	}

	private void createButtons(final Composite parent) {
		createAddButton(parent);
		createEditButton(parent);
		createRemoveButton(parent);
		createCustomButtons(parent);
	}

	protected TableViewer getViewer() {
		return viewer;
	}

	protected IObservableValue getViewerSelectionValue() {
		if (viewerSingleSelectionValue == null) {
			viewerSingleSelectionValue = ViewersObservables
					.observeSingleSelection(getViewer());
		}
		return viewerSingleSelectionValue;
	}

	protected void visitViewerSelection(final String operationName,
			final Visitor<T> visitor) {
		final IStructuredSelection viewerSelection = (IStructuredSelection) viewer
				.getSelection();

		final IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@SuppressWarnings("unchecked")
			public void run(final IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask(operationName, viewerSelection.size());
					final SubMonitor progress = SubMonitor
							.convert(monitor, 100);
					final Iterator<?> it = viewerSelection.iterator();
					while (it.hasNext()) {
						if (progress.isCanceled()) {
							throw new InterruptedException();
						}
						visitor.visit((T) it.next(), progress.newChild(1));
					}
				} finally {
					monitor.done();
				}
			}
		};
		SafeRunner.run(new SafeRunnable(operationName) {
			public void run() throws Exception {
				final ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						getPage().getShell());
				dialog.run(true, true, runnable);
			}
		});
	}

	protected void enableWithSelection(final Control control, final int type) {
		bindToViewerSelection(SWTObservables.observeEnabled(control), type);
	}

	protected void bindToViewerSelection(final IObservableValue target,
			final int type) {
		Assert.isLegal((type & SWT.MULTI | SWT.SINGLE) != 0);
		if (ctx != null) {
			ctx.bindValue(target, getViewerSelectionValue(), null,
					selectionToBooleanConverter());
		}
	}

	/**
	 * Allows subclasses to add custom buttons after the standard
	 * add/edit/remove group of buttons.
	 * 
	 * <p>
	 * The default implementation does nothing.
	 * 
	 * @param parent
	 *            to create the buttons on
	 */
	protected void createCustomButtons(final Composite parent) {
	}

	protected Composite getButtonBoxControl(final Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			final GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					buttonBox = null;
				}
			});

		} else {
			checkParent(buttonBox, parent);
		}

		// selectionChanged();
		return buttonBox;
	}

	@Override
	public void load() {
		doLoad();
		setPresentsDefaultValue(false);
		refreshValidState();
	}

	@SuppressWarnings("unchecked")
	protected List<T> getItems() {
		return items;
	}

	protected void add(final T item) {
		setPresentsDefaultValue(false);
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.add(item);
			}
		});
	}

	protected void remove(final T item) {
		setPresentsDefaultValue(false);
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.remove(item);
			}
		});
	}

	@Override
	public void loadDefault() {
		items.clear();
		doLoadDefault();
		setPresentsDefaultValue(true);
		refreshValidState();
	}

	@Override
	public void store() {
		doStore();
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	protected boolean isReadOnly() {
		return ((flags & READ_ONLY) == READ_ONLY);
	}
}

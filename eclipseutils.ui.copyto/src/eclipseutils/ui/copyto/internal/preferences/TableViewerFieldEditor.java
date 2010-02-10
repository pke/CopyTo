package eclipseutils.ui.copyto.internal.preferences;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

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
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
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
import org.eclipse.jface.viewers.EditingSupport;
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
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Table;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import eclipseutils.ui.copyto.internal.Target;

/**
 * An abstract field editor that manages a master/detail table of input values.
 * The editor displays a table containing the values, buttons for adding and
 * removing values.
 * <p>
 * Subclasses must implement the abstract framework methods.
 * </p>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            Type of the items the editor displays.
 */
public abstract class TableViewerFieldEditor<T> extends FieldEditor {

	private TableViewer viewer;
	private Composite buttonBox;
	private IObservableList items;
	private DataBindingContext ctx;
	private IObservableValue viewerSelectionValue;
	private Sash sash;
	private Composite detailClient;

	protected static interface Visitor<T> {
		void visit(T item, IProgressMonitor monitor);
	}

	protected TableViewerFieldEditor(String preferencePath, String labelText,
			Composite parent) {
		super(preferencePath, labelText, parent);
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	protected abstract String[] getColumnNames();

	protected abstract String[] getColumnLabels();

	protected abstract String getId(T item);

	protected abstract void store(T item, Preferences node);

	public Table getTableControl(Composite parent) {
		if (viewer == null) {
			Composite client = new Composite(parent, SWT.NULL);
			// client.setLayout(new FillLayout(SWT.VERTICAL));
			TableColumnLayout tableLayout = new TableColumnLayout();
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
				public void widgetDisposed(DisposeEvent e) {
					ctx.dispose();
				}
			});
			String[] columnNames = getColumnNames();
			String[] columnLables = getColumnLabels();

			for (int i = 0; i < columnNames.length; ++i) {
				TableViewerColumn viewerColumn = new TableViewerColumn(viewer,
						SWT.LEFT);
				viewerColumn.getColumn().setText(columnLables[i]);
				viewerColumn.setEditingSupport(createEditingSupport(
						columnNames[i], viewer, ctx));
				tableLayout.setColumnData(viewerColumn.getColumn(),
						new ColumnWeightData(i == columnNames.length - 1 ? 100
								: 30));
			}
			items = new WritableList();
			ViewerSupport.bind(viewer, items, BeanProperties.values(
					Target.class, columnNames));
			// table.addSelectionListener(getSelectionListener());
			table.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					viewer = null;
				}
			});
		} else {
			checkParent(viewer.getControl(), parent);
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
	protected EditingSupport createEditingSupport(String name,
			TableViewer viewer, DataBindingContext context) {
		return null;
	}

	@Override
	public void setEnabled(boolean enabled, Composite parent) {
		super.setEnabled(enabled, parent);
		getTableControl(parent).setEnabled(enabled);

		for (Control control : this.buttonBox.getChildren()) {
			control.setEnabled(enabled);
		}
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) viewer.getControl().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		GridDataFactory.fillDefaults().span(numColumns, 1).applyTo(
				getLabelControl(parent));

		Table table = getTableControl(parent);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.heightHint = 300;
		gd.grabExcessHorizontalSpace = true;
		table.getParent().setLayoutData(gd);

		buttonBox = getButtonBoxControl(parent);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(
				buttonBox);
	}

	protected abstract T createItem(Preferences preferences);

	protected Button createPushButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button,
				IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT,
				SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return button;
	}

	UpdateValueStrategy selectionToBooleanConverter() {
		UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
		modelToTarget.setConverter(new Converter(Object.class, boolean.class) {
			public Object convert(Object fromObject) {
				return fromObject != null;
			}
		});
		return modelToTarget;
	}

	protected Button createAddButton(Composite parent) {
		Button button = createPushButton(parent, JFaceResources
				.getString("ListEditor.add"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				T item = createItem(null);
				if (item != null) {
					add(item);
				}
			}
		});
		return button;
	}

	protected Button createRemoveButton(Composite parent) {
		Button button = createPushButton(parent, JFaceResources
				.getString("ListEditor.remove"));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				visitViewerSelection("Removing selected elements",
						new Visitor<T>() {
							public void visit(final T item,
									IProgressMonitor monitor) {
								remove(item);
							}
						});
			}
		});
		enableWithSelection(button, SWT.MULTI | SWT.SINGLE);
		return button;
	}

	private void createButtons(Composite parent) {
		createAddButton(parent);
		createRemoveButton(parent);
		createCustomButtons(parent);
	}

	protected TableViewer getViewer() {
		return viewer;
	}

	protected IObservableValue getViewerSelectionValue() {
		if (viewerSelectionValue == null) {
			viewerSelectionValue = ViewersObservables
					.observeSingleSelection(viewer);
		}

		return viewerSelectionValue;
	}

	protected void visitViewerSelection(final String operationName,
			final Visitor<T> visitor) {
		final IStructuredSelection viewerSelection = (IStructuredSelection) viewer
				.getSelection();

		final IRunnableWithProgress runnable = new IRunnableWithProgress() {

			@SuppressWarnings("unchecked")
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask(operationName, viewerSelection.size());
					SubMonitor progress = SubMonitor.convert(monitor, 100);
					Iterator<?> it = viewerSelection.iterator();
					while (it.hasNext()) {
						if (progress.isCanceled()) {
							throw new InterruptedException();
						}
						T item = (T) it.next();
						visitor.visit(item, progress.newChild(1));
					}
				} finally {
					monitor.done();
				}
			}
		};
		SafeRunner.run(new SafeRunnable(operationName) {
			public void run() throws Exception {
				ProgressMonitorDialog dialog = new ProgressMonitorDialog(
						getPage().getShell());
				dialog.run(true, true, runnable);
			}
		});
	}

	protected void enableWithSelection(Control control, int type) {
		bindToViewerSelection(SWTObservables.observeEnabled(control), type);
	}

	protected void bindToViewerSelection(IObservableValue target, int type) {
		Assert.isLegal((type & SWT.MULTI | SWT.SINGLE) != 0);
		if (ctx != null) {
			IObservableValue modelValue = getViewerSelectionValue();
			ctx.bindValue(target, getViewerSelectionValue(), null,
					selectionToBooleanConverter());
		}
	}

	protected void createCustomButtons(Composite parent) {
	}

	public Composite getButtonBoxControl(Composite parent) {
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
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
		setPresentsDefaultValue(false);
		final Preferences node = new InstanceScope()
				.getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (BackingStoreException e) {
		}
		refreshValidState();
	}

	/**
	 * The default implementation calls {@link #createItem(Preferences)} with
	 * each child node of the given <i>instanceNode</i>.
	 * 
	 * Sub-classes may overwrite to provide a different behavior.
	 * 
	 * @param instanceNode
	 * @param childrenNames
	 */
	protected void doLoad(Preferences instanceNode, String... childrenNames) {
		for (String key : childrenNames) {
			T item = createItem(instanceNode.node(key));
			if (item != null) {
				add(item);
			}
		}
	}

	/**
	 * The default implementation just calls
	 * {@link #doLoad(Preferences, String...)}
	 * 
	 * @param defaultNode
	 * @param childrenNames
	 */
	protected void doLoadDefault(Preferences defaultNode,
			String... childrenNames) {
		doLoad();
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
		Preferences node = new DefaultScope().getNode(getPreferenceName());
		try {
			doLoad(node, node.childrenNames());
		} catch (BackingStoreException e) {
		}
		setPresentsDefaultValue(true);
		refreshValidState();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void store() {
		InstanceScope instanceScope = new InstanceScope();
		Preferences node = instanceScope.getNode(getPreferenceName());
		try {
			node.removeNode();
			if (!presentsDefaultValue()) {
				node = instanceScope.getNode(getPreferenceName());
				Iterator<T> it = items.iterator();
				while (it.hasNext()) {
					T item = it.next();
					store(item, node.node(getId(item)));
				}
				node.flush();
			}
		} catch (BackingStoreException e) {
		}
	}

	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/**
	 * Not called, since we do not have a PreferenceStore set.
	 */
	@Override
	protected void doLoad() {
		throw new IllegalAccessError();
	}

	/**
	 * Not called, since we do not have a PreferenceStore set.
	 */
	@Override
	protected void doLoadDefault() {
		throw new IllegalAccessError();
	}

	/**
	 * Not called, since we do not have a PreferenceStore set.
	 */
	@Override
	protected void doStore() {
		throw new IllegalAccessError();
	}
}

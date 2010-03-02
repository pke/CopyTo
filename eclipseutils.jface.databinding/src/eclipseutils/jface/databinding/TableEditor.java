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

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import eclipseutils.jface.preferences.Messages;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class TableEditor<T> {
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
	private Composite client;

	protected static interface Visitor<T> {
		void visit(T item, IProgressMonitor monitor);
	}

	protected TableEditor(final Composite parent, Collection<T> items,
			final int flags) {
		this.flags = flags;
		this.items = items != null ? (items instanceof IObservableList) ? (IObservableList) items
				: new WritableList(items, null)
				: new WritableList();

		client = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(client);
		createTableControl(client);
		GridDataFactory.fillDefaults().hint(200, 200).grab(true, true).applyTo(
				getViewer().getControl().getParent());
		buttonBox = getButtonBoxControl(client);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(
				buttonBox);
	}

	public Control getControl() {
		return client;
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

	protected int getTableStyles() {
		return SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.FULL_SELECTION;
	}

	/**
	 * @param parent
	 * @return the table
	 */
	void createTableControl(final Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);
		// client.setLayout(new FillLayout(SWT.VERTICAL));
		final TableColumnLayout tableLayout = new TableColumnLayout();
		client.setLayout(tableLayout);
		int tableStyles = getTableStyles();
		viewer = (tableStyles & SWT.CHECK) == SWT.CHECK ? CheckboxTableViewer
				.newCheckList(client, tableStyles) : new TableViewer(client,
				tableStyles);

		final Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(parent.getFont());

		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removeSelected();
				}
			}
		});

		ctx = new DataBindingContext();
		table.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				ctx.dispose();
				viewer = null;
			}
		});
		final String[] columnNames = getColumnNames();
		final String[] columnLables = getColumnLabels();

		for (int i = 0; i < columnNames.length; ++i) {
			final TableViewerColumn viewerColumn = new TableViewerColumn(
					viewer, SWT.LEFT);
			viewerColumn.getColumn().setText(columnLables[i]);
			viewerColumn.setEditingSupport(createEditingSupport(columnNames[i],
					viewer, ctx));
			tableLayout
					.setColumnData(viewerColumn.getColumn(),
							new ColumnWeightData(
									i == columnNames.length - 1 ? 100 : 30));
		}
		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		viewer.setContentProvider(contentProvider);
		viewer.setLabelProvider(getLabelProvider(contentProvider));
		viewer.setInput(items);
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(final DoubleClickEvent event) {
				editSelection();
			}
		});
	}

	protected IBaseLabelProvider getLabelProvider(
			ObservableListContentProvider contentProvider) {
		return new ObservableMapLabelProvider(Properties.observeEach(
				contentProvider.getKnownElements(), BeanProperties
						.values(getColumnNames())));
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

	public void setEnabled(final boolean enabled) {
		getViewer().getControl().setEnabled(enabled);

		for (final Control control : this.buttonBox.getChildren()) {
			control.setEnabled(enabled);
		}
	}

	/**
	 * Returns the number of pixels corresponding to the given number of
	 * horizontal dialog units.
	 * <p>
	 * Clients may call this framework method, but should not override it.
	 * </p>
	 * 
	 * @param control
	 *            the control being sized
	 * @param dlus
	 *            the number of horizontal dialog units
	 * @return the number of pixels
	 */
	protected int convertHorizontalDLUsToPixels(Control control, int dlus) {
		GC gc = new GC(control);
		gc.setFont(control.getFont());
		int averageWidth = gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();

		double horizontalDialogUnitSize = averageWidth * 0.25;

		return (int) Math.round(dlus * horizontalDialogUnitSize);
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
	protected Button createAddButton(final Composite parent) {
		if (isReadOnly()) {
			return null;
		}
		final Button button = createPushButton(parent, JFaceResources.getString("ListEditor.add")); //$NON-NLS-1$
		createAddButtonMenu(button);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				Menu menu = button.getMenu();
				if (menu != null) {
					final Rectangle point = button.getBounds();
					Point pos = button.toDisplay(point.x, point.height);
					button.getMenu().setLocation(pos.x, pos.y);
					button.getMenu().setVisible(true);
				} else {
					final T item = createItem(button.getShell());
					if (item != null) {
						add(item, true);
					}
				}
			}
		});
		return button;
	}
	
	protected void fillAddButtonMenu(IMenuManager menu) {
	}

	protected Button createRemoveButton(final Composite parent) {
		if (isReadOnly()) {
			return null;
		}
		final Button button = createPushButton(parent, JFaceResources.getString(JFaceResources.getString("ListEditor.remove"))); //$NON-NLS-1$
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (MessageDialog.openQuestion(button.getShell(),
						Messages.AbstractTableViewerFieldEditor_Remove_Title,
						Messages.AbstractTableViewerFieldEditor_Remove_Message)) {
					removeSelected();
				}
			}
		});
		enableWithSelection(button, SWT.MULTI | SWT.SINGLE);
		return null;
	}

	protected Button createEditButton(final Composite parent) {
		final Button button = createPushButton(parent,
				Messages.AbstractTableViewerFieldEditor_Edit_Label);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				editSelection();
			}
		});
		enableWithSelection(button, SWT.SINGLE);
		return button;
	}

	@SuppressWarnings("unchecked")
	private void editSelection() {
		if ((flags & NO_EDIT_BUTTON) == NO_EDIT_BUTTON) {
			return;
		}
		final T item = (T) getViewerSelectionValue().getValue();
		if (item != null) {
			editItem(this.getViewer().getControl().getShell(), item);
		}
	}

	private void createButtons(final Composite parent) {
		createAddButton(parent);
		createEditButton(parent);
		createRemoveButton(parent);
		createCustomButtons(parent);
	}

	public TableViewer getViewer() {
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
						getViewer().getControl().getShell());
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
			IObservableValue selected = new ComputedValue(Boolean.TYPE) {
				protected Object calculate() {
					return Boolean
							.valueOf(getViewerSelectionValue().getValue() != null);
				}
			};
			ctx.bindValue(target, selected);
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
			GridLayoutFactory.fillDefaults().applyTo(buttonBox);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					buttonBox = null;
				}
			});
		}
		// selectionChanged();
		return buttonBox;
	}

	@SuppressWarnings("unchecked")
	public List<T> getItems() {
		return items;
	}

	public void clear() {
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.clear();
			}
		});
	}

	public void add(final T item, final boolean select) {
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.add(item);
				if (select) {
					viewer.setSelection(new StructuredSelection(item));
				}
			}
		});
	}

	public void remove(final T item) {
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.remove(item);
			}
		});
	}

	public void addAll(final Collection<T> adds) {
		items.getRealm().exec(new Runnable() {
			public void run() {
				items.addAll(adds);
			}
		});
	}

	protected boolean isReadOnly() {
		return ((flags & READ_ONLY) == READ_ONLY);
	}

	private void removeSelected() {
		visitViewerSelection(
				Messages.AbstractTableViewerFieldEditor_Remove_JobName,
				new Visitor<T>() {
					public void visit(final T item,
							final IProgressMonitor monitor) {
						remove(item);
					}
				});
	}

	protected void createAddButtonMenu(Control button) {
		MenuManager menu = new MenuManager("#PopupMenu");
		menu.setRemoveAllWhenShown(true);
		menu.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				fillAddButtonMenu(manager);
			}
		});
		button.setMenu(menu.createContextMenu(button));
	}
}

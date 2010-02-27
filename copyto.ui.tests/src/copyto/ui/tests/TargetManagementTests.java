package copyto.ui.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

import copyto.core.TargetBooleanParam;
import copyto.core.TargetParam;
import copyto.core.models.BooleanTargetParamModel;
import copyto.core.models.StringTargetParamModel;
import copyto.core.models.TargetParamsModel;

public class TargetManagementTests {

	public class TargetParamsViewer {
		private TableViewer viewer;
		private DataBindingContext dbx;
		private IObservableValue viewerSingleSelectionValue;
		private IObservableList items;
		private IViewerObservableList viewerMultiSelectionList;

		public TargetParamsViewer(Composite parent, TargetParamsModel model) {
			dbx = new DataBindingContext();
			parent.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event) {
					dbx.dispose();
				}
			});
			items = BeansObservables.observeList(model, "params",
					TargetParam.class);
			createViewer(parent);
			createButtons(parent);
		}

		protected void createViewer(Composite parent) {
			final Composite client = new Composite(parent, SWT.NULL);
			// client.setLayout(new FillLayout(SWT.VERTICAL));
			final TableColumnLayout tableLayout = new TableColumnLayout();
			client.setLayout(tableLayout);

			viewer = new TableViewer(client, SWT.BORDER | SWT.MULTI
					| SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);

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
			viewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					editSelected();
				}
			});

			Map<String, String> columns = getColumns();
			for (Entry<String, String> entry : columns.entrySet()) {
				final TableViewerColumn viewerColumn = new TableViewerColumn(
						viewer, SWT.LEFT);
				viewerColumn.getColumn().setText(entry.getValue());
				tableLayout.setColumnData(viewerColumn.getColumn(),
						new ColumnWeightData(10));
			}

			ViewerSupport.bind(viewer, items, BeanProperties.values(columns
					.keySet().toArray(new String[columns.size()])));
		}

		protected Map<String, String> getColumns() {
			Map<String, String> columns = new LinkedHashMap<String, String>();
			columns.put("name", "Name");
			columns.put("class.name", "Type");
			columns.put("stringValue", "Value");
			return columns;
		}

		UpdateValueStrategy selectionToBooleanConverter() {
			final UpdateValueStrategy modelToTarget = new UpdateValueStrategy();
			modelToTarget.setConverter(new Converter(Object.class,
					boolean.class) {
				public Object convert(final Object fromObject) {
					return fromObject != null;
				}
			});
			return modelToTarget;
		}

		protected void editSelected() {
		}

		protected void removeSelected() {
			Iterator<?> it = getViewerMultiSelectionList().iterator();
			while (it.hasNext()) {
				items.remove(it.next());
			}
		}

		protected IObservableValue getViewerSelectionValue() {
			if (viewerSingleSelectionValue == null) {
				viewerSingleSelectionValue = ViewersObservables
						.observeSingleSelection(viewer);
			}
			return viewerSingleSelectionValue;
		}

		protected IObservableList getViewerMultiSelectionList() {
			if (viewerMultiSelectionList == null) {
				viewerMultiSelectionList = ViewersObservables
						.observeMultiSelection(viewer);
			}
			return viewerMultiSelectionList;
		}

		protected void createButtons(Composite parent) {
			Composite client = new Composite(parent, SWT.NULL);
			GridLayoutFactory.swtDefaults().applyTo(client);
			Button button = new Button(client, SWT.PUSH);
			button.setText("Add...");

			button = new Button(client, SWT.PUSH);
			button.setText("Edit...");
			dbx.bindValue(SWTObservables.observeEnabled(button),
					getViewerSelectionValue(), null,
					selectionToBooleanConverter());

			button = new Button(client, SWT.PUSH);
			button.setText("Remove");
			dbx.bindValue(SWTObservables.observeEnabled(button),
					getViewerSelectionValue(), null,
					selectionToBooleanConverter());
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					removeSelected();
				}
			});
		}
	}

	@Test
	public void saveTargetParams() throws Exception {
		TargetParamsModel model = new TargetParamsModel();
		model.getParams().add(
				new StringTargetParamModel("paste_code", "${copyto.text}"));
		model.getParams().add(
				new BooleanTargetParamModel("paste_private", true, "P"));
		InstanceScope instanceScope = new InstanceScope();
		IPath path = instanceScope.getLocation();
		Preferences preferences = instanceScope.getNode(getClass().getName());
		model.save(preferences);
		preferences.flush();
	}

	public class DialogBuilder {
		private TargetParamsModel model;
		private Map<String, String> params;

		DialogBuilder(Composite parent, TargetParamsModel model) {
			Composite client = new Composite(parent, SWT.NULL);
			GridLayoutFactory.swtDefaults().numColumns(2).applyTo(client);
			this.model = model;

			final DataBindingContext dbx = new DataBindingContext();
			parent.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(final DisposeEvent event) {
					dbx.dispose();
				}
			});
			params = new HashMap<String, String>();
			final IMapProperty selfMap = Properties.selfMap(String.class,
					String.class);
			final IObservableMap observableParams = selfMap.observe(params);

			for (final TargetParam<?> param : model.getParams()) {
				final IObservableValue observeMapEntry = Observables
						.observeMapEntry(observableParams, param.getName());
				Label label = new Label(parent, SWT.LEFT);
				label.setText(param.getName());
				IObservableValue controlObservable = createControl(client,
						param);
				UpdateValueStrategy targetToModel = new UpdateValueStrategy()
						.setConverter(new Converter(Object.class, String.class) {
							public Object convert(Object fromObject) {
								return param.getValue().toString();
							}
						});
				params.put(param.getName(), param.getValue().toString());
				dbx.bindValue(controlObservable, observeMapEntry, null, null);
			}
		}

		private IObservableValue createControl(Composite parent,
				TargetParam<?> param) {
			if (param instanceof TargetBooleanParam) {
				Button control = new Button(parent, SWT.CHECK);
				control.setText(param.getName());
				return SWTObservables.observeSelection(control);
			} else {
				Text control = new Text(parent, SWT.BORDER);
				return SWTObservables.observeText(control);
			}
		}

		public Map<String, String> getParams() {
			return params;
		}
	}

	@Test
	public void dialogText() {
		final Display display = new Display();
		Realm.runWithDefault(SWTObservables.getRealm(display), new Runnable() {
			public void run() {
				final Shell shell = new Shell(display);
				shell.setLayout(new FillLayout());
				TargetParamsModel model = new TargetParamsModel();
				model.getParams().add(
						new StringTargetParamModel("paste_code",
								"${copyto.text}"));
				model.getParams()
						.add(
								new BooleanTargetParamModel("paste_private",
										true, "P"));
				// new TargetParamsViewer(shell, model);
				DialogBuilder dialog = new DialogBuilder(shell, model);
				shell.setSize(400, 200);
				shell.open();
				// The SWT event loop
				Display display = Display.getCurrent();
				while (!shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}

				Map<String, String> params = dialog.getParams();
				for (Entry<String, String> entry : params.entrySet()) {
					System.out.println(entry.getKey() + " => "
							+ entry.getValue());
				}
			}
		});
	}

}

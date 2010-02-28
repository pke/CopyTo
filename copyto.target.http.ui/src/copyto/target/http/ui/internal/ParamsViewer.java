package copyto.target.http.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.NativePropertyListener;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.CellEditorProperties;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import copyto.core.TargetParam;
import copyto.core.models.BooleanTargetParamModel;
import copyto.core.models.ChoiceTargetParamModel;
import copyto.core.models.StringTargetParamModel;
import copyto.target.http.core.HttpTarget;
import copyto.target.http.core.internal.html.form.Form;
import copyto.target.http.core.internal.html.form.FormParser;
import copyto.target.http.core.internal.html.form.HtmlElement;
import copyto.target.http.core.internal.html.form.SelectElement;
import copyto.target.http.core.internal.html.form.TextAreaElement;
import eclipseutils.jface.databinding.TableEditor;

class ParamsViewer extends TableEditor<TargetParam<?>> {

	private IObservableValue hostValue;

	@SuppressWarnings("unchecked")
	protected ParamsViewer(HttpTarget target, IObservableValue hostValue, Composite parent) {
		super(parent, BeansObservables.observeList(target, "params", TargetParam.class), 0);
		this.hostValue = hostValue;
	}
	
	@Override
	protected int getTableStyles() {
		return super.getTableStyles() | SWT.CHECK;
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { "name", "labelValue", "readOnly" };
	}

	@Override
	protected String[] getColumnLabels() {
		return new String[] { "Name", "Value", "Show in UI" };
	}

	@Override
	protected TargetParam<?> createItem(Shell shell) {
		return new StringTargetParamModel("name", "value");
	}

	class ParamValueEditingSupport extends ObservableValueEditingSupport {
		TableViewer viewer;

		public ParamValueEditingSupport(TableViewer viewer,
				DataBindingContext dbc) {
			super(viewer, dbc);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (element instanceof BooleanTargetParamModel) {
				return new CheckboxCellEditor(viewer.getTable());
			}
			if (element instanceof ChoiceTargetParamModel) {
				String[] values = ((ChoiceTargetParamModel) element)
						.getValues();
				return new ComboBoxCellEditor(viewer.getTable(), values);
			}
			return new TextCellEditor(viewer.getTable());
		}

		@Override
		protected IObservableValue doCreateCellEditorObservable(
				CellEditor cellEditor) {
			if (cellEditor instanceof TextCellEditor) {
				return SWTObservables.observeText(cellEditor.getControl(),
						SWT.Modify);
			}
			if (cellEditor instanceof ComboBoxCellEditor
					|| cellEditor instanceof CheckboxCellEditor) {
				return SWTObservables.observeSelection(cellEditor.getControl());
			}
			return null;
		}

		@Override
		protected IObservableValue doCreateElementObservable(Object element,
				ViewerCell cell) {
			if (element instanceof ChoiceTargetParamModel) {
				final ChoiceTargetParamModel targetParam = (ChoiceTargetParamModel) element;
				return new WritableValue(targetParam.getLabelValue(),
						String.class) {
					@Override
					public void doSetValue(Object value) {
						targetParam.setValue(value.toString());
					}
				};
			}
			return BeansObservables.observeValue(element, "value");
		}
	}

	@Override
	protected IBaseLabelProvider getLabelProvider(
			ObservableListContentProvider contentProvider) {
		return new ObservableMapLabelProvider(Properties.observeEach(
				contentProvider.getKnownElements(), BeanProperties
						.values(getColumnNames()))) {
			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == 1
						&& element instanceof ChoiceTargetParamModel) {
					ChoiceTargetParamModel param = (ChoiceTargetParamModel) element;
					if (!param.isKnownValue()) {
						return FieldDecorationRegistry.getDefault()
								.getFieldDecoration(
										FieldDecorationRegistry.DEC_WARNING)
								.getImage();
					}
				}
				return super.getColumnImage(element, columnIndex);
			}

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 2) {
					return ((TargetParam<?>) element).isReadOnly() ? "yes"
							: "no";
				}
				return super.getColumnText(element, columnIndex);
			}
		};
	}

	public class CellEditorValueProperty extends SimpleValueProperty {
		private final CellEditor editor;
		private final Object valueType;

		/**
		 * @param editor
		 * @param valueType
		 */
		public CellEditorValueProperty(CellEditor editor, Object valueType) {
			this.editor = editor;
			this.valueType = valueType;
		}

		public Object getValueType() {
			return valueType;
		}

		protected Object doGetValue(Object source) {
			return ((CellEditor) source).getValue();
		}

		protected void doSetValue(Object source, Object value) {
			((CellEditor) source).setValue(value);
		}

		public INativePropertyListener adaptListener(
				ISimplePropertyListener listener) {
			return new Listener(listener);
		}

		private class Listener extends NativePropertyListener implements
				ICellEditorListener {
			public Listener(ISimplePropertyListener listener) {
				super(CellEditorValueProperty.this, listener);
			}

			protected void doAddTo(Object source) {
				Assert.isTrue(editor == source, "Expected " + editor //$NON-NLS-1$
						+ " as property source but received " + source); //$NON-NLS-1$
				editor.addListener(this);
			}

			protected void doRemoveFrom(Object source) {
				Assert.isTrue(editor == source, "Expected " + editor //$NON-NLS-1$
						+ " as property source but received " + source); //$NON-NLS-1$
				editor.removeListener(this);
			}

			public void applyEditorValue() {
				fireChange(editor, null);
			}

			public void cancelEditor() {

			}

			public void editorValueChanged(boolean oldValidState,
					boolean newValidState) {
				fireStale(editor);
			}
		}

		public String toString() {
			return "CellEditor.value <" + valueType + ">"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	@Override
	protected EditingSupport createEditingSupport(String name,
			TableViewer viewer, DataBindingContext context) {
		if ("name".equals(name)) {
			IValueProperty cellEditorControlText = CellEditorProperties
					.control().value(WidgetProperties.text());
			return ObservableValueEditingSupport.create(viewer, context,
					new TextCellEditor(viewer.getTable()),
					cellEditorControlText, BeanProperties.value(
							TargetParam.class, name));
		}
		if ("labelValue".equals(name)) {
			return new ParamValueEditingSupport(viewer, context);
		}

		if ("readOnly".equals(name)) {
			CheckboxCellEditor cellEditor = new CheckboxCellEditor(viewer
					.getTable());
			IValueProperty cellEditorControlText = new CellEditorValueProperty(
					cellEditor, boolean.class);
			return ObservableValueEditingSupport.create(viewer, context,
					cellEditor, cellEditorControlText, BeanProperties.value(
							TargetParam.class, name));
		}
		return super.createEditingSupport(name, viewer, context);
	}
	
	@Override
	protected void createCustomButtons(Composite parent) {
		final Button button = createPushButton(parent, "Auto Detect...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FormParser parser = new FormParser();
				try {
					URL url = new URL(hostValue.getValue()
							.toString());
					Collection<Form> forms = parser.parse(url);
					if (forms.size() > 0) {
						Form form = forms.size() == 1 ? forms.iterator().next() : AutoDetectResultDialog.select(button.getShell(), url, forms);
						if (form != null) {
							clear();
							for (HtmlElement element : form.getElements()) {
								String value = element
										.getAttribute("value", "");
								if (element instanceof TextAreaElement) {
									value = "${copyto.text}";
								}
								TargetParam<?> param;
								if (element instanceof SelectElement) {
									SelectElement selectElement = (SelectElement)element;
									param = new ChoiceTargetParamModel(element.getName(), selectElement.getOptions());
									((ChoiceTargetParamModel)param).setValue(selectElement.getSelected());
								} else {
									param = new StringTargetParamModel(
											element.getName(), value);
								}
								add(param);
							}
							hostValue.setValue(url.toString()
									+ form.getAction());
						}
					}
				} catch (MalformedURLException e) {
				}
			}
		});
	}
	
	@Override
	protected Button createAddButton(Composite parent) {
		final Button button = createPushButton(parent, "Ne&w..."); //$NON-NLS-1$
		Menu menu = new Menu(button);
		
		MenuItem menuItem = new MenuItem(menu, 0);
		menuItem.setText("String");
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				add(new StringTargetParamModel("name", "value"));
			}
		});
		
		menuItem = new MenuItem(menu, 0);
		menuItem.setText("Boolean");
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				add(new BooleanTargetParamModel("name", true, Boolean.toString(true)));
			}
		});
		
		menuItem = new MenuItem(menu, 0);
		menuItem.setText("Choice");
		menuItem.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent event) {
				add(new ChoiceTargetParamModel("name", Collections.EMPTY_MAP));
			}
		});
		button.setMenu(menu);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final Rectangle point = ((Button) event.widget).getBounds();
				//event.x = point.x;
				//event.y = point.height;
				Point pos = button.toDisplay(point.x, point.height);
				button.getMenu().setLocation(pos.x, pos.y);
				button.getMenu().setVisible(true);
			}
		});
		return button;
	}
}
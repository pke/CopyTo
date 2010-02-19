package copyto.ui.history.internal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.MultiListProperty;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import copyto.core.Result;

public class HistoryViewer extends TreeViewer {

	private WritableList items;

	protected String[] getColumnNames() {
		return new String[] { "target.name" };
	}

	protected String[] getColumnLabels() {
		return new String[] { "Name" };
	}

	public HistoryViewer(Composite parent, int style) {
		super(parent, style);

		final String[] columnNames = getColumnNames();
		final String[] columnLables = getColumnLabels();
		Assert.isTrue(columnNames.length == columnLables.length);

		final TreeColumnLayout tableLayout = new TreeColumnLayout();
		parent.setLayout(tableLayout);

		final Tree tree = getTree();
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setFont(parent.getFont());

		final DataBindingContext ctx = new DataBindingContext();
		tree.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				ctx.dispose();
			}
		});

		for (int i = 0; i < columnNames.length; ++i) {
			final TreeViewerColumn viewerColumn = new TreeViewerColumn(this,
					SWT.LEFT);
			viewerColumn.getColumn().setText(columnLables[i]);
			tableLayout
					.setColumnData(viewerColumn.getColumn(),
							new ColumnWeightData(
									i == columnNames.length - 1 ? 100 : 30));
		}
		items = new WritableList();
		MultiListProperty childrenProp = new MultiListProperty(
				new IListProperty[] { PojoProperties.list("successes"),
						PojoProperties.list("failures") });
		/*
		 * ViewerSupport.bind(viewer, items, childrenProp, BeanProperties
		 * .values(columnNames));
		 */

		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(
				childrenProp.listFactory(), null);
		setContentProvider(contentProvider);

		ObservableMapLabelProvider labelProvider = new ObservableMapLabelProvider(
				BeanProperties.value("target.name").observeDetail(
						contentProvider.getKnownElements())) {

			public String getText(Object element) {
				if (element instanceof Result) {
				}
				return super.getText(element);
			}
		};
		setLabelProvider(labelProvider);
		setInput(items);
	}

	public IObservableList getItems() {
		return items;
	}
}

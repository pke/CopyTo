package copyto.ui.history.internal;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.MultiListProperty;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;


import copyto.core.Result;
import copyto.core.Results;
import copyto.ui.UIResultHandler;

public class HistoryViewPart extends ViewPart implements UIResultHandler {

	private TreeViewer viewer;
	private ServiceRegistration serviceRegistration;
	private WritableList items;

	@Override
	public void createPartControl(Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);

		viewer = new TreeViewer(client, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.FULL_SELECTION);

		final TreeColumnLayout tableLayout = new TreeColumnLayout();
		client.setLayout(tableLayout);

		final Tree table = viewer.getTree();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setFont(parent.getFont());

		final DataBindingContext ctx = new DataBindingContext();
		table.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e) {
				ctx.dispose();
				viewer = null;
			}
		});
		final String[] columnNames = new String[] { "target.name" };
		final String[] columnLables = new String[] { "Target" };

		for (int i = 0; i < columnNames.length; ++i) {
			final TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer,
					SWT.LEFT);
			viewerColumn.getColumn().setText(columnLables[i]);
			tableLayout
					.setColumnData(viewerColumn.getColumn(),
							new ColumnWeightData(
									i == columnNames.length - 1 ? 100 : 30));
		}
		items = new WritableList();
		MultiListProperty childrenProp = new MultiListProperty(
				new IListProperty[] {
						PojoProperties.list("successes"),
						PojoProperties.list("failures") });
		/*ViewerSupport.bind(viewer, items, childrenProp, BeanProperties
				.values(columnNames));*/
		
		ObservableListTreeContentProvider contentProvider = new ObservableListTreeContentProvider(
				childrenProp.listFactory(), null);
		viewer.setContentProvider(contentProvider);

		ObservableMapLabelProvider labelProvider = new ObservableMapLabelProvider(
				BeanProperties.value("target.name").observeDetail(
						contentProvider.getKnownElements())) {
			
			public String getText(Object element) {
				if (element instanceof Result) {
				}
				return super.getText(element);
			}
		};
		viewer.setLabelProvider(labelProvider);
		viewer.setInput(items);

		serviceRegistration = FrameworkUtil.getBundle(getClass())
				.getBundleContext().registerService(
						UIResultHandler.class.getName(), this, null);
	}

	@Override
	public void dispose() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
		super.dispose();
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	public void handleResults(Results result, Shell shell) {
		items.add(result);
	}
}

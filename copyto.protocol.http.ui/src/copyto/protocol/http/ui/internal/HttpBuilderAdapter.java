package copyto.protocol.http.ui.internal;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.internal.databinding.provisional.swt.ControlUpdater;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import copyto.protocol.http.core.HttpTarget;
import copyto.protocol.http.core.ResponseHandler;
import copyto.protocol.http.core.ResponseHandlerDescriptor;
import copyto.protocol.http.core.internal.ResponseHandlers;
import eclipseutils.jface.databinding.Builder;
import eclipseutils.jface.databinding.BuilderAdapter;
import eclipseutils.jface.databinding.ControlCreator;
import eclipseutils.jface.databinding.FieldOptions;
import eclipseutils.jface.databinding.FieldOptions.ControlCustomizer;
import eclipseutils.jface.databinding.GridLayoutBuilder;
import eclipseutils.jface.databinding.validators.URLValidator;

final class HttpBuilderAdapter implements BuilderAdapter {

	public Builder create(Object bean, Builder parentBuilder) {
		final HttpTarget target = (HttpTarget) bean;
		final IObservableValue hostText[] = new IObservableValue[1];
		return parentBuilder.field("host",
				new FieldOptions(new FieldOptions.ControlCustomizer() {
					public void customizeControl(Control control,
							IObservableValue observableValue,
							FieldOptions options) {
						hostText[0] = SWTObservables.observeText(control);
					}
				}).setValidator(URLValidator.getInstance())).selection(
				"responseHandlerDescriptor",
				new FieldOptions(ResponseHandlers.getInstance().findAll())
						.setControlCustomizer(new ControlCustomizer() {
							public void customizeControl(Control control,
									final IObservableValue observableValue,
									FieldOptions options) {
								final Composite client = new Composite(control
										.getParent(), SWT.NULL);
								final StackLayout layout = new StackLayout();
								client.setLayout(layout);
								final Composite emptyComposite = new Composite(
										client, SWT.NULL);
								IAdapterManager adapterManager = Platform
										.getAdapterManager();
								for (Object o : options.getItems()) {
									try {
										ResponseHandler handler = ((ResponseHandlerDescriptor) o)
												.createResponseHandler();
										Composite contentClient = new Composite(
												client, SWT.NULL);
										contentClient.setData("handler",
												o);
										BuilderAdapter builder = (BuilderAdapter) adapterManager
												.loadAdapter(handler,
														BuilderAdapter.class
																.getName());
										if (builder != null) {
											builder.create(
													handler,
													new GridLayoutBuilder(
															contentClient,
															handler,
															UpdateValueStrategy.POLICY_UPDATE));
										}
									} catch (CoreException e) {
									}
								}
								observableValue.addChangeListener(new IChangeListener() {
									public void handleChange(ChangeEvent event) {
										event.getObservable().getRealm().exec(new Runnable() {
											public void run() {
												for (Control c : client.getChildren()) {
													if (observableValue.getValue().equals(c.getData("handler"))) {
														layout.topControl = c;
														client.layout();												
														return;
													}
												}
												layout.topControl = emptyComposite;
												client.layout();
											}
										});
									}
								});
							}
						})).control(new ControlCreator() {
			public Control create(Composite parent, Object bean) {
				Composite client = new Composite(parent, SWT.NULL);
				GridLayoutFactory.fillDefaults().applyTo(client);
				Label label = new Label(client, SWT.LEFT);
				label.setText("Parameters" + ":");
				final ParamsViewer viewer = new ParamsViewer(target,
						hostText[0], client);
				GridDataFactory.fillDefaults().grab(true, true).applyTo(
						viewer.getControl());
				return client;
			}
		});
	}
}
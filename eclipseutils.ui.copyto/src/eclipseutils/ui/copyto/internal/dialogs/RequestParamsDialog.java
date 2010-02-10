package eclipseutils.ui.copyto.internal.dialogs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.IParameterValues;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.databinding.property.map.IMapProperty;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.internal.CopyToHandler;

public class RequestParamsDialog extends TitleAreaDialog {
	private final Map<String, ?> params;
	private final String id;

	public RequestParamsDialog(final Shell parentShell, String id,
			Map<String, ?> params) {
		super(parentShell);
		this.params = params;
		this.id = id;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final DataBindingContext dbx = new DataBindingContext();
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				dbx.dispose();
			}
		});

		final IMapProperty selfMap = Properties.selfMap(String.class,
				String.class);
		final IObservableMap observableParams = selfMap.observe(params);

		final Map<String, IConfigurationElement> paramInfos = new HashMap<String, IConfigurationElement>();

		// Add all handler params to the paramInfos map first, as we will
		// later iterate over it
		final Iterator<String> it = params.keySet().iterator();
		while (it.hasNext()) {
			paramInfos.put(it.next(), null);
		}

		final IConfigurationElement[] configurationElements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						FrameworkUtil.getBundle(getClass()).getSymbolicName(),
						CopyToHandler.COMMAND_TARGET_PARAM, id);
		for (final IConfigurationElement configurationElement : configurationElements) {
			if ("paramInfos".equals(configurationElement.getName())) {
				final IConfigurationElement[] paramConfigs = configurationElement
						.getChildren("paramInfo");
				for (IConfigurationElement paramInfoConfig : paramConfigs) {
					paramInfos.put(paramInfoConfig.getAttribute("name"),
							paramInfoConfig);
				}
				final String hiddenAttribute = configurationElement
						.getAttribute("hidden");
				if (hiddenAttribute != null && hiddenAttribute.length() > 0) {
					for (String key : hiddenAttribute.split(",")) {
						paramInfos.remove(key);
					}
				}
			}
		}

		final Composite client = new Composite((Composite) super
				.createDialogArea(parent), SWT.NULL);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(client);

		for (Entry<String, IConfigurationElement> entry : paramInfos.entrySet()) {
			final String key = entry.getKey();

			final IObservableValue controlObservable[] = { null };

			Runnable editorCreator = new Runnable() {
				public void run() {
					final Text text = new Text(client, SWT.SINGLE | SWT.BORDER);
					controlObservable[0] = SWTObservables.observeText(text);
					// text.setText(entry.getValue().toString());
				}
			};
			final String defaultLabelText = key.toString();
			String labelText = defaultLabelText;
			String desc = null;
			final IConfigurationElement configElement = entry.getValue();
			if (configElement == null) {
				continue;
			}
			final String text = configElement.getAttribute("label");
			if (text != null && text.length() > 0) {
				labelText = text;
			}
			desc = configElement.getAttribute("description");
			final String className = configElement.getAttribute("type");
			if ("bool".equals(className) || "boolean".equals(className)
					|| Boolean.class.getName().equals(className)
					|| boolean.class.getName().equals(className)) {
				editorCreator = new Runnable() {
					public void run() {
						final Button button = new Button(client, SWT.CHECK);
						controlObservable[0] = SWTObservables
								.observeSelection(button);
					}
				};
			} else if (className != null) {
				try {
					final Object typeInstance = configElement
							.createExecutableExtension("type");
					if (typeInstance instanceof IParameterValues) {
						editorCreator = new Runnable() {
							public void run() {
								final ComboViewer combo = new ComboViewer(
										client, SWT.DROP_DOWN);
								combo.setContentProvider(ArrayContentProvider
										.getInstance());
								controlObservable[0] = SWTObservables
										.observeText(combo.getControl());
								final Map<?, ?> params = ((IParameterValues) typeInstance)
										.getParameterValues();
								combo.setInput(params.values());
							}
						};
					}
				} catch (final Exception e) {
					continue;
				}
			}
			final Label label = new Label(client, SWT.RIGHT);
			label.setText(labelText);
			if (desc != null && desc.length() > 0) {
				label.setToolTipText(desc);
			}
			editorCreator.run();
			if (controlObservable[0] != null) {
				final IObservableValue observeMapEntry = Observables
						.observeMapEntry(observableParams, key);
				dbx.bindValue(controlObservable[0], observeMapEntry);
			}
		}

		return client;
	}
}
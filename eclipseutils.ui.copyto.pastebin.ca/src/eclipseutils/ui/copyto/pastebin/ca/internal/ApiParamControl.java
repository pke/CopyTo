/**
 * 
 */
package eclipseutils.ui.copyto.pastebin.ca.internal;

import java.net.URL;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import eclipseutils.ui.copyto.CustomExtensionParamControl;

/**
 * Creates a read-only text field and button to query a new API key.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * @since 0.1
 */
public class ApiParamControl extends CustomExtensionParamControl {

	public IObservableValue createControl(Composite parent) {
		Composite client = new Composite(parent, SWT.NULL);
		Label label = new Label(client, SWT.NULL);
		label.setText(getLabelText());
		label.setToolTipText(getDescription());
		Text apiText = new Text(client, SWT.READ_ONLY);
		Button newButton = new Button(client, SWT.PUSH);
		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					URL apiURL = new URL("http://pastebin.ca/api");
					apiURL.openStream();
				} catch (Exception e) {
				}
			}
		});
		return SWTObservables.observeText(apiText);
	}

}

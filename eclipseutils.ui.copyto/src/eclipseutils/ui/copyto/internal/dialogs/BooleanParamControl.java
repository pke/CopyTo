package eclipseutils.ui.copyto.internal.dialogs;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import eclipseutils.ui.copyto.api.CustomParamControl;

/**
 * Creates a boolean control on the standard grid.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class BooleanParamControl implements CustomParamControl {
	private final String label;
	private final String desc;

	public BooleanParamControl(String label, String desc) {
		this.label = label;
		this.desc = desc;
	}

	public IObservableValue createControl(Composite parent) {
		final Button button = new Button(parent, SWT.CHECK);
		button.setText(label);
		if (desc != null) {
			button.setToolTipText(desc);
		}
		GridDataFactory.swtDefaults().span(2, 1).applyTo(button);
		return SWTObservables.observeSelection(button);
	}
}
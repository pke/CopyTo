package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class FormCreatorToolkit implements CreatorToolkit {

	private final FormToolkit toolkit;

	public FormCreatorToolkit(final FormToolkit toolkit) {
		this.toolkit = toolkit;
	}

	public Button createButton(final Composite parent, final String text,
			final int style) {
		return toolkit.createButton(parent, text, style);
	}

	public Text createText(final Composite parent, final String text,
			final int style) {
		return toolkit.createText(parent, text, style);
	}

	public Label createLabel(final Composite parent, final String text,
			final int style) {
		return toolkit.createLabel(parent, text, style);
	}
}

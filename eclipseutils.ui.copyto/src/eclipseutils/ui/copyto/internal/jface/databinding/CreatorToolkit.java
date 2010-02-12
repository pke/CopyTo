package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public interface CreatorToolkit {
	Button createButton(Composite parent, String text, int style);

	Text createText(final Composite parent, String text, int style);

	Label createLabel(Composite parent, String text, int style);
}

package eclipseutils.jface.databinding.customizers;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import eclipseutils.jface.databinding.FieldOptions.ControlCustomizer;

/**
 * Selects all text in a text control when it gains focus.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class SelectAllOnFocus implements ControlCustomizer {

	public void customizeControl(final Control control) {
		if (control instanceof Text) {
			final Text text = (Text) control;
			control.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(final FocusEvent e) {
					if (!text.isDisposed()) {
						(text).selectAll();
					}
				}
			});
		}
	}

}

package eclipseutils.jface.databinding.customizers;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import eclipseutils.jface.databinding.FieldOptions;
import eclipseutils.jface.databinding.FieldOptions.ControlCustomizer;

/**
 * Selects all text in a text control when it gains focus.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class SelectAllOnFocus implements ControlCustomizer {

	private final boolean always;

	/**
	 * @param always
	 *            select all text or only on first time the focus is gained.
	 */
	public SelectAllOnFocus(final boolean always) {
		this.always = always;
	}

	/**
	 * Creates a customizer that always selects the text when the control
	 * receives the focus.
	 */
	public SelectAllOnFocus() {
		this(true);
	}

	public void customizeControl(final Control control, IObservableValue observableValue, FieldOptions options) {
		if (control instanceof Text) {
			final Text text = (Text) control;
			control.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(final FocusEvent e) {
					if (!text.isDisposed()) {
						(text).selectAll();
					}
					if (!always) {
						control.removeFocusListener(this);
					}
				}
			});
		}
	}

}

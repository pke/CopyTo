package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.swt.widgets.Composite;

public interface BuilderProvider {
	Builder createBuilder(Composite parent);
}

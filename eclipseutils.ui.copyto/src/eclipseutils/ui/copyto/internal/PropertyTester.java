package eclipseutils.ui.copyto.internal;

import org.eclipse.jface.text.ITextSelection;

public class PropertyTester extends org.eclipse.core.expressions.PropertyTester {

	public boolean test(final Object receiver, final String property,
			final Object[] args, final Object expectedValue) {
		if (receiver instanceof ITextSelection) {
			final ITextSelection selection = (ITextSelection) receiver;
			return selection.getLength() > 0;
		}
		return false;
	}

}

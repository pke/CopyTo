package eclipseutils.ui.copyto.codepad.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

public class CodepadLanguageResolver implements IDynamicVariableResolver {

	public String resolveValue(final IDynamicVariable variable, final String argument) throws CoreException {
		if ("text/c".equals(argument)) { //$NON-NLS-1$
			return "C"; //$NON-NLS-1$
		}
		return "Plain Text"; //$NON-NLS-1$
	}

}

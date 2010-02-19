package copyto.target.pastebin.com.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class FormatResolver implements IDynamicVariableResolver {

	public String resolveValue(final IDynamicVariable variable,
			final String argument) throws CoreException {
		if (argument != null && argument.startsWith("text/java")) { //$NON-NLS-1$
			return "java"; //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}

}

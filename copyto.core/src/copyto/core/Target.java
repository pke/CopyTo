package copyto.core;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 
 * A targets post paramters are saved in a Map<String, Object>
 * 
 * The following SWT controls are created for each type of Object:
 * 
 * <pre>
 * StringParam  - Text
 * BooleanParam - Button(SWT.PUSH)
 * MapParam     - Combo(SWT.LIST)
 * </pre>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Target extends Persistable, IAdaptable {

	String getName();

	String getId();

	// String getUrl();

	Results transfer(IProgressMonitor monitor, Copyable... copyables);

	Protocol getProtocol();
}

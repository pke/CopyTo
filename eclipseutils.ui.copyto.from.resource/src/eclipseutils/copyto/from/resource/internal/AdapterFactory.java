package eclipseutils.copyto.from.resource.internal;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;

import eclipseutils.ui.copyto.api.Copyable;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adapterType == Copyable.class
				&& adaptableObject instanceof IResource) {
			final IPath location = ((IResource) adaptableObject).getLocation();
			if (location != null) {
				try {
					return new ResourceCopyable(adaptableObject, location);
				} catch (final CoreException e) {
				}
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

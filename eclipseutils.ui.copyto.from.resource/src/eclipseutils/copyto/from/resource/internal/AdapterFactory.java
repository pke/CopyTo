package eclipseutils.copyto.from.resource.internal;

import java.net.URLConnection;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

import eclipseutils.ui.copyto.api.Copyable;

public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adapterType == Copyable.class
				&& adaptableObject instanceof IResource) {
			final IPath location = ((IResource) adaptableObject).getLocation();
			if (location != null) {
				try {
					ITextFileBufferManager.DEFAULT.connect(location,
							LocationKind.LOCATION, null);
					return new Copyable() {
						private String text;

						public String getText() {
							if (this.text == null) {
								final ITextFileBuffer textFileBuffer = ITextFileBufferManager.DEFAULT
										.getTextFileBuffer(location,
												LocationKind.LOCATION);
								final IDocument document = textFileBuffer
										.getDocument();
								this.text = document.get();
								try {
									ITextFileBufferManager.DEFAULT.disconnect(
											location, LocationKind.LOCATION,
											null);
								} catch (final CoreException e) {
								}
							}
							return this.text;
						}

						public String getMimeType() {
							final String extension = location
									.getFileExtension();
							if ("java".equals(extension)) { //$NON-NLS-1$
								return "text/java"; //$NON-NLS-1$
							}
							return URLConnection
									.guessContentTypeFromName(location
											.toOSString());
						}

						public Object getSource() {
							return adaptableObject;
						}
					};
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

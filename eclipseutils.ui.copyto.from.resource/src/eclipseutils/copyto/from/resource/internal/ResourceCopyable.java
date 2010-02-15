package eclipseutils.copyto.from.resource.internal;

import java.net.URLConnection;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

import eclipseutils.ui.copyto.api.Copyable;

final class ResourceCopyable implements Copyable {
	private final Object adaptableObject;
	private final IPath location;
	private String text;

	public ResourceCopyable(final Object adaptableObject, final IPath location)
			throws CoreException {
		ITextFileBufferManager.DEFAULT.connect(location, LocationKind.LOCATION,
				null);
		this.adaptableObject = adaptableObject;
		this.location = location;
	}

	public String getMimeType() {
		final String extension = location.getFileExtension();
		if ("java".equals(extension)) { //$NON-NLS-1$
			return "text/java"; //$NON-NLS-1$
		}
		return URLConnection.guessContentTypeFromName(location.toOSString());
	}

	public Object getSource() {
		return adaptableObject;
	}

	public String getText() {
		if (this.text == null) {
			final ITextFileBuffer textFileBuffer = ITextFileBufferManager.DEFAULT
					.getTextFileBuffer(location, LocationKind.LOCATION);
			final IDocument document = textFileBuffer.getDocument();
			this.text = document.get();
			try {
				ITextFileBufferManager.DEFAULT.disconnect(location,
						LocationKind.LOCATION, null);
			} catch (final CoreException e) {
			}
		}
		return this.text;
	}
}
package eclipseutils.ui.copyto.from.jdt.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import eclipseutils.ui.copyto.api.Copyable;

public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adaptableObject instanceof AbstractTextEditor) {
			final AbstractTextEditor textEditor = (AbstractTextEditor) adaptableObject;
			final ITypeRoot element = JavaUI.getEditorInputTypeRoot(textEditor
					.getEditorInput());
			if (element != null) {
				return new RangeCopyable(element, textEditor
						.getHighlightRange());
				/*
				 * final ITextViewer textViewer = EditorHelper
				 * .getSourceViewer(textEditor); if (textViewer instanceof
				 * ISourceViewer) { return new SourceViewerCopyable(element,
				 * (ISourceViewer) textViewer); }
				 */
			}
		} else if (adaptableObject instanceof IMember) {
			return new MemberCopyable((IMember) adaptableObject);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return new Class[] { Copyable.class };
	}

}

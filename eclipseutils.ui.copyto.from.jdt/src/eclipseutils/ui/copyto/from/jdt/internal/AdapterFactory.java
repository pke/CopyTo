package eclipseutils.ui.copyto.from.jdt.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import eclipseutils.ui.copyto.api.Copyable;

/**
 * Adapts from AbstractTextEditor selection and IMember to Copyable.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		if (adaptableObject instanceof AbstractTextEditor) {
			final AbstractTextEditor textEditor = (AbstractTextEditor) adaptableObject;
			// TODO: Check if editor is selected but *no* Text Selection
			final IRegion highlightRange = textEditor.getHighlightRange();
			if (highlightRange != null) {
				final ITypeRoot element = JavaUI
						.getEditorInputTypeRoot(textEditor.getEditorInput());
				if (element != null) {
					return new RangeCopyable(element, highlightRange);
				}
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

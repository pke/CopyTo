package eclipseutils.ui.copyto.from.jdt.internal;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

class SourceViewerCopyable extends ASTNodeCopyable {
	private final ISourceViewer viewer;
	private final ITypeRoot root;

	public SourceViewerCopyable(final ITypeRoot root, final ISourceViewer viewer) {
		this.viewer = viewer;
		this.root = root;
	}

	@Override
	protected ASTNode createNode() {
		final Point selectedRange[] = { null };
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				selectedRange[0] = viewer.getSelectedRange();
			}
		});
		final int length = selectedRange[0].y;
		final int offset = selectedRange[0].x;

		final CompilationUnit ast = SharedASTProvider.getAST(root,
				SharedASTProvider.WAIT_YES, null);
		if (ast == null) {
			return null;
		}

		final NodeFinder finder = new NodeFinder(ast, offset, length);
		return finder.getCoveringNode();
	}
}

package copyto.from.jdt.internal;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IRegion;

class RangeCopyable extends ASTNodeCopyable {

	private final ITypeRoot root;
	private final IRegion range;

	public RangeCopyable(final ITypeRoot element, final IRegion range) {
		this.root = element;
		this.range = range;
	}

	@Override
	protected ASTNode createNode() {
		final CompilationUnit ast = SharedASTProvider.getAST(root,
				SharedASTProvider.WAIT_YES, null);
		if (ast == null) {
			return null;
		}

		final NodeFinder finder = new NodeFinder(ast, range.getOffset(), range
				.getLength());
		return finder.getCoveringNode();
	}

}

package eclipseutils.ui.copyto.from.jdt.internal;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.ui.SharedASTProvider;

class MemberCopyable extends ASTNodeCopyable {

	private final IMember member;

	public MemberCopyable(final IMember member) {
		this.member = member;
	}

	@Override
	protected ASTNode createNode() {
		final CompilationUnit ast = SharedASTProvider.getAST(this.member
				.getTypeRoot(), SharedASTProvider.WAIT_YES, null);
		if (ast != null) {
			try {
				return findMethodDeclaration(ast, this.member);
			} catch (final JavaModelException e) {
			}
			return null;
		}
		return ast;
	}

	private static ASTNode findMethodDeclaration(final CompilationUnit unit,
			final IMember member) throws JavaModelException {
		final ISourceRange sourceRange = member.getSourceRange();
		return NodeFinder.perform(unit, sourceRange.getOffset(), sourceRange
				.getLength());
	}

}
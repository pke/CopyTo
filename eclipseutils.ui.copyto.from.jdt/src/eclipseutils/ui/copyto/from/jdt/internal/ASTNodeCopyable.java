package eclipseutils.ui.copyto.from.jdt.internal;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;

import eclipseutils.ui.copyto.api.Copyable;

abstract class ASTNodeCopyable implements Copyable {
	private ASTNode node;

	public String getText() {
		final ASTNode node = getNode();
		return node != null ? node.toString() : ""; //$NON-NLS-1$
	}

	protected ASTNode getNode() {
		if (this.node == null) {
			this.node = normalize(createNode());
		}
		return this.node;
	}

	private static ASTNode normalize(final ASTNode node) {
		if (node instanceof Name || node instanceof Block
				|| node instanceof PrimitiveType || node instanceof Modifier
				|| node instanceof PackageDeclaration) {
			return normalize(node.getParent());
		}
		return node;
	}

	public String getMimeType() {
		return "text/java"; //$NON-NLS-1$
	}

	protected abstract ASTNode createNode();

	public Object getSource() {
		return getNode();
	}
}

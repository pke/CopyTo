package copyto.from.jdt.internal;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;

import copyto.core.Copyable;

abstract class ASTNodeCopyable implements Copyable {
	private ASTNode node;

	/**
	 * @return always <code>text/java</code>.
	 */
	public String getMimeType() {
		return "text/java"; //$NON-NLS-1$
	}

	public Object getSource() {
		return getNode();
	}

	public String getText() {
		final ASTNode node = getNode();
		return node != null ? node.toString() : ""; //$NON-NLS-1$
	}

	/**
	 * Subclasses implement this and will be able to create a node based on
	 * their (constructor) input data.
	 * 
	 * @return the start node, that will be normalized and used in
	 *         {@link #getText()}.
	 */
	protected abstract ASTNode createNode();

	protected ASTNode getNode() {
		if (this.node == null) {
			this.node = normalize(createNode());
		}
		return this.node;
	}

	/**
	 * Walks up the hierarchy to a node that has reasonable text content to
	 * copy.
	 * <p>
	 * I.e: if the current node contains a primitive type, it returns the parent
	 * (which can be a class, method or block).
	 * 
	 * @param node
	 *            to normalize. Can be <code>null</code>.
	 * @return the normalized node.
	 */
	private static ASTNode normalize(final ASTNode node) {
		if ((node instanceof Name) || (node instanceof Block)
				|| (node instanceof PrimitiveType)
				|| (node instanceof Modifier)
				|| (node instanceof PackageDeclaration)) {
			return normalize(node.getParent());
		}
		return node;
	}
}

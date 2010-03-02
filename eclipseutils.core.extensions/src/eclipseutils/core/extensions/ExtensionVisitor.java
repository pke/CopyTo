/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package eclipseutils.core.extensions;

import org.eclipse.core.runtime.IConfigurationElement;

import patterns.visitor.Visitor;



/**
 * A visitor for {@link IConfigurationElement}
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public abstract class ExtensionVisitor<R> implements
		Visitor<IConfigurationElement, R> {

	private final String[] requiredAttributes;

	public ExtensionVisitor(String... requiredAttributes) {
		this.requiredAttributes = requiredAttributes;
	}

	public final R visit(IConfigurationElement item) {
		if (requiredAttributes != null) {
			for (String name : requiredAttributes) {
				if (null == item.getAttribute(name)) {
					return null;
				}
			}
		}
		return create(item);
	}

	protected abstract R create(IConfigurationElement configElement);

}

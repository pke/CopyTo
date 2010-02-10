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
package eclipseutils.ui.copyto.api;

/**
 * 
 * @author <a href="mailto:kursawe@topsystem.de">Philipp Kursawe</a>
 * 
 */
public interface Copyable {
	/**
	 * Implementation should lazy initialize and cache the returned text, as
	 * this is called at least twice.
	 * 
	 * @return textual representation.
	 */
	String getText();

	/**
	 * @return the mime type. Must <em>never</em> return <code>null</code>;
	 */
	String getMimeType();

	/**
	 * @return The source this Copyable was created from. Must never be
	 *         <code>null</code>.
	 */
	Object getSource();
}

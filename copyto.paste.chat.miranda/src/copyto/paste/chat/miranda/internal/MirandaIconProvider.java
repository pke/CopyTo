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
package copyto.paste.chat.miranda.internal;

import org.eclipse.jface.resource.ImageDescriptor;

import copyto.paste.chat.miranda.MirandaIRC;
import copyto.ui.IconProvider;

/**
 * Extracts the miranda logo out of the miranda32.exe file.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 *
 */
public class MirandaIconProvider implements IconProvider {

	public ImageDescriptor getIcon() {
		MirandaIRC miranda = MirandaIRC.find();
		return miranda.getIcon();
	}
}

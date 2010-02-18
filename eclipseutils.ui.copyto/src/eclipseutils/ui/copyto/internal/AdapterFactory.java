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

package eclipseutils.ui.copyto.internal;

import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;

import eclipseutils.ui.copyto.api.Copyable;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class AdapterFactory implements IAdapterFactory {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Object adaptableObject,
			final Class adapterType) {
		/*if (adaptableObject instanceof IAdaptable) {
			adaptableObject = ((IAdaptable) adaptableObject)
					.getAdapter(IMarker.class);
		}*/
		if (adaptableObject instanceof IMarker) {
			final IMarker marker = (IMarker) adaptableObject;
			return new Copyable() {

				@SuppressWarnings("unchecked")
				public String getText() {
					final String NEWLINE = System.getProperty("line.separator"); //$NON-NLS-1$
					final char DELIMITER = '\t';

					final StringBuffer text = new StringBuffer();
					try {
						final Set<Entry<String, Object>> entrySet = marker
								.getAttributes().entrySet();
						for (final Entry<String, Object> entry : entrySet) {
							text.append(entry.getKey());
							text.append(':');
							text.append(entry.getValue().toString().replaceAll(
									"#", " "));
							text.append(NEWLINE);
						}
					} catch (final CoreException e) {
					}
					return text.toString();
				}

				public String getMimeType() {
					return "text/plain"; //$NON-NLS-1$
				}

				public Object getSource() {
					return marker;
				}
			};
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

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
package copyto.protocol.http.ui.internal;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;

import copyto.protocol.http.core.ResponseHandler;
import eclipseutils.jface.databinding.Builder;
import eclipseutils.jface.databinding.BuilderAdapter;
import eclipseutils.jface.databinding.FieldOptions;
import eclipseutils.jface.databinding.validators.AbstractValidator;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class BodyResponseHandlerAdapters implements IAdapterFactory {

	public class BodyBuilderAdapter implements BuilderAdapter {
		
		private IValidator regExValidator = new AbstractValidator(IStatus.ERROR) {
			
			@Override
			protected String performValidation(Object value) throws Throwable {
				try {
					String regex = value.toString().trim();
					Pattern.compile(regex);
				} catch (PatternSyntaxException e) {
					return e.getDescription();
				}
				return null;
			}
		};

		public Builder create(Object bean, Builder parentBuilder) {
			return parentBuilder.field("regex", new FieldOptions(regExValidator));
		}

	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ResponseHandler) {
			if (adapterType == BuilderAdapter.class) {
				return new BodyBuilderAdapter();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Class[] getAdapterList() {
		return null;
	}

}

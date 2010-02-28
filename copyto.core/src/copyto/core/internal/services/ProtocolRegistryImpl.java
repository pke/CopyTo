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
package copyto.core.internal.services;

import java.util.Collection;

import org.eclipse.core.runtime.IConfigurationElement;

import copyto.core.TargetFactories;
import copyto.core.TargetFactory;
import copyto.core.TargetFactoryDescriptor;
import eclipseutils.core.extensions.BaseExtensionDescriptor;
import eclipseutils.core.extensions.ExtensionPoints;
import eclipseutils.core.extensions.ExtensionVisitor;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ProtocolRegistryImpl implements TargetFactories {

	private static final String ID_ATT = "id";
	private static final String COPYTO_CORE_PROTOCOLS = "copyto.core.protocols";

	private class ProtocolDescriptorImpl extends BaseExtensionDescriptor
			implements TargetFactoryDescriptor {
		private TargetFactory protocol;

		public ProtocolDescriptorImpl(IConfigurationElement configElement) {
			super(configElement);
		}

		public TargetFactory getFactory() {
			if (null == protocol) {
				try {
					protocol = createExecutableExtension();
				} catch (Exception e) {
				}
			}
			return protocol;
		}
	}

	public TargetFactoryDescriptor find(final String id) {
		return ExtensionPoints.find(COPYTO_CORE_PROTOCOLS, ID_ATT, id,
				new ExtensionVisitor<TargetFactoryDescriptor>() {
					@Override
					public TargetFactoryDescriptor create(
							IConfigurationElement config) {
						return new ProtocolDescriptorImpl(config);
					}
				});
	}

	public Collection<TargetFactoryDescriptor> findAll() {
		return ExtensionPoints.visitAll(COPYTO_CORE_PROTOCOLS,
				new ExtensionVisitor<TargetFactoryDescriptor>(ID_ATT) {
					@Override
					protected TargetFactoryDescriptor create(
							IConfigurationElement configElement) {
						return new ProtocolDescriptorImpl(configElement);
					}
				});
	}
}

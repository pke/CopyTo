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
import org.eclipse.core.runtime.PlatformObject;

import copyto.core.Protocol;
import copyto.core.ProtocolDescriptor;
import copyto.core.ProtocolRegistry;
import eclipseutils.core.extensions.ExtensionPoints;
import eclipseutils.core.extensions.ExtensionVisitor;
import eclipseutils.core.extensions.Visitor;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ProtocolRegistryImpl implements ProtocolRegistry {

	private static final String ID_ATT = "id";
	private static final String COPYTO_CORE_PROTOCOLS = "copyto.core.protocols";

	private class ProtocolDescriptorImpl extends PlatformObject implements
			ProtocolDescriptor {
		private static final String CLASS_ATT = "class";
		private static final String NAME_ATT = "name";
		private final IConfigurationElement configElement;
		private Protocol protocol;

		public ProtocolDescriptorImpl(IConfigurationElement configElement) {
			this.configElement = configElement;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public Object getAdapter(Class adapter) {
			if (adapter == IConfigurationElement.class) {
				return configElement;
			}
			return super.getAdapter(adapter);
		}

		public String getId() {
			return configElement.getAttribute(ID_ATT);
		}

		public String getName() {
			return configElement.getAttribute(NAME_ATT);
		}

		public Protocol getProtocol() {
			if (null == protocol) {
				try {
					protocol = (Protocol) configElement
							.createExecutableExtension(CLASS_ATT);
				} catch (Exception e) {
				}
			}
			return protocol;
		}
	}

	public ProtocolDescriptor find(final String id) {
		return ExtensionPoints.find(COPYTO_CORE_PROTOCOLS, ID_ATT, id,
				new Visitor<IConfigurationElement, ProtocolDescriptor>() {
					public ProtocolDescriptor visit(IConfigurationElement config) {
						return new ProtocolDescriptorImpl(config);
					}
				});
	}

	public Collection<ProtocolDescriptor> findAll() {
		return ExtensionPoints.visitAll(COPYTO_CORE_PROTOCOLS,
				new ExtensionVisitor<ProtocolDescriptor>(ID_ATT) {
					@Override
					protected ProtocolDescriptor create(
							IConfigurationElement configElement) {
						return new ProtocolDescriptorImpl(configElement);
					}
				});
	}
}

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
package copyto.ui.internal.preferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import osgiutils.services.DefaultCollectionServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import copyto.core.Target;
import copyto.core.TargetDescriptor;
import copyto.core.TargetService;
import eclipseutils.jface.databinding.TableEditor;
import eclipseutils.jface.preferences.AbstractTableViewerFieldEditor;

class TargetFieldEditor extends AbstractTableViewerFieldEditor<Target> {

	TargetFieldEditor(final String preferencePath, final String labelText,
			final Composite parent) {
		super(preferencePath, labelText, parent);
	}

	@Override
	protected Collection<Target> loadItems() {
		return Trackers.run(TargetService.class,
				new DefaultCollectionServiceRunnable<TargetService, Target>() {
					public Collection<Target> run(final TargetService service) {
						List<TargetDescriptor> descs = service.findAll();
						Collection<Target> items = new ArrayList<Target>(descs.size());
						for (TargetDescriptor desc : descs) {
							items.add(desc.createTarget());
						}
						return items;
					}
				});
	}

	@Override
	protected void doLoadDefault() {
		doLoad();
	}

	@Override
	protected void doStore() {
		Trackers.run(TargetService.class,
				new SimpleServiceRunnable<TargetService>() {
					@Override
					public void doRun(final TargetService service) {
						service.save(getItems());
					}
				});
	}

	@Override
	protected TableEditor<Target> createEditorControl(Composite parent) {
		return new TargetEditor(parent, null, 0);
	}
}
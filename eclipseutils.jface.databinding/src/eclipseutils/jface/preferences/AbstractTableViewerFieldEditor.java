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
package eclipseutils.jface.preferences;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import eclipseutils.jface.databinding.TableEditor;

/**
 * An abstract field editor that manages a master/detail table of input values.
 * 
 * <p>
 * The editor displays a table containing the values, buttons for adding and
 * removing values.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 * @param <T>
 *            Type of the items the editor displays.
 */
public abstract class AbstractTableViewerFieldEditor<T> extends FieldEditor {

	private TableEditor<T> editor;
	
	protected AbstractTableViewerFieldEditor(final String preferencePath,
			final String labelText, final Composite parent) {
		super(preferencePath, labelText, parent);		
	}

	@Override
	public void setFocus() {
		if (editor != null) {
			editor.getViewer().getControl().setFocus();
		}
	}
	
	@Override
	public void setEnabled(final boolean enabled, final Composite parent) {
		super.setEnabled(enabled, parent);
		getEditorControl(parent).setEnabled(enabled);
	}

	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) editor.getControl().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		GridDataFactory.fillDefaults().span(numColumns, 1).applyTo(
				getLabelControl(parent));

		final Control table = getEditorControl(parent);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.heightHint = 300;
		gd.grabExcessHorizontalSpace = true;
		table.setLayoutData(gd);
	}

	
	protected Control getEditorControl(Composite parent) {
		if (editor == null) {
			editor = createEditorControl(parent);			
		} else {
			checkParent(editor.getControl(), parent);
		}
		return editor.getControl();
	}

	protected abstract TableEditor<T> createEditorControl(Composite parent);

	@Override
	protected void doLoad() {
		final Collection<T> loadedItems = loadItems();
		if (loadedItems != null) {
			editor.addAll(loadedItems);
		}
	}

	protected abstract Collection<T> loadItems();

	protected List<T> getItems() {
		return editor.getItems();
	}

	protected void add(final T item) {
		setPresentsDefaultValue(false);
		editor.add(item, true);
	}

	protected void remove(final T item) {
		setPresentsDefaultValue(false);
		editor.remove(item);
	}

	@Override
	public final void loadDefault() {
		editor.clear();
		doLoadDefault();
		setPresentsDefaultValue(true);
		refreshValidState();
	}

	@Override
	public final void store() {
		doStore();
	}

	@Override
	public final int getNumberOfControls() {
		return 2;
	}
}

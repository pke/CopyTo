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

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.FrameworkUtil;


import copyto.core.Target;
import copyto.ui.internal.ClipboardResultsHandler;
import copyto.ui.internal.Messages;
import copyto.ui.internal.commands.CopyToHandler;


/**
 * Preference page for CopyTo.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class CopyToPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * 
	 */
	public static String ID = "copyto.PreferencePage"; //$NON-NLS-1$

	private IWorkbench workbench;

	/**
	 * 
	 */
	public CopyToPreferencePage() {
		final IPreferenceStore prefs = new ScopedPreferenceStore(
				new ConfigurationScope(), FrameworkUtil.getBundle(getClass())
						.getSymbolicName());
		setPreferenceStore(prefs);
		noDefaultAndApplyButton();
	}

	static class TargetWorkbenchAdapter extends WorkbenchAdapter {

		@Override
		public String getLabel(final Object object) {
			return ((Target) object).getName();
		}

		static IWorkbenchAdapter instance;

		static IWorkbenchAdapter getInstance() {
			if (instance == null) {
				instance = new TargetWorkbenchAdapter();
			}
			return instance;
		}
	}

	@Override
	public boolean performOk() {
		final boolean result = super.performOk();
		if (result) {
			final ICommandService cs = (ICommandService) workbench
					.getService(ICommandService.class);
			cs.refreshElements(CopyToHandler.COMMAND_ID, null);
		}
		return result;
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(
				ClipboardResultsHandler.CLIPBOARD_ALWAYS_OVERWRITE,
				Messages.CopyToPreferencePage_AlwaysCopyToClipboard,
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new TargetFieldEditor(
				FrameworkUtil.getBundle(getClass()).getSymbolicName()
						+ "/targets", Messages.CopyToPreferencePage_TargetsLabel, getFieldEditorParent())); //$NON-NLS-1$
	}

	public void init(final IWorkbench workbench) {
		this.workbench = workbench;
	}

	/**
	 * Shows this preference page on the given shell.
	 * 
	 * @param shell
	 *            can be <code>null</code>, then the active workbench window
	 *            will be used.
	 */
	public static void show(final Shell shell) {
		PreferencesUtil.createPreferenceDialogOn(shell, ID,
				new String[] { ID }, null).open();
	}
}

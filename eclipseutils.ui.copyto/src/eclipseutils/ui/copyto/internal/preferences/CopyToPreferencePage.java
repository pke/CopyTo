package eclipseutils.ui.copyto.internal.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.FrameworkUtil;

import eclipseutils.ui.copyto.internal.Target;
import eclipseutils.ui.copyto.internal.results.ClipboardResultsHandler;

/**
 * Preference page for CopyTo.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class CopyToPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private IWorkbench workbench;

	/**
	 * 
	 */
	public CopyToPreferencePage() {
		final IPreferenceStore prefs = new ScopedPreferenceStore(
				new InstanceScope(), FrameworkUtil.getBundle(getClass())
						.getSymbolicName());
		setPreferenceStore(prefs);
		noDefaultAndApplyButton();
	}

	static class TargetWorkbenchAdapter extends WorkbenchAdapter {

		@Override
		public String getLabel(Object object) {
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
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(
				ClipboardResultsHandler.CLIPBOARD_ALWAYS_OVERWRITE,
				"Always copy result URLs to clipboard",
				BooleanFieldEditor.DEFAULT, getFieldEditorParent()));
		addField(new TargetFieldEditor(FrameworkUtil.getBundle(getClass())
				.getSymbolicName()
				+ "/targets", "Targets", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}
}

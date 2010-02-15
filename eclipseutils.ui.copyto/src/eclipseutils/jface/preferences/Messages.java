package eclipseutils.jface.preferences;

import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eclipseutils.jface.preferences.messages"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static String AbstractTableViewerFieldEditor_Edit_Label;
	/**
	 * 
	 */
	public static String AbstractTableViewerFieldEditor_Remove_JobName;
	/**
	 * 
	 */
	public static String AbstractTableViewerFieldEditor_Remove_Message;
	/**
	 * 
	 */
	public static String AbstractTableViewerFieldEditor_Remove_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

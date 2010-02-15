package eclipseutils.ui.copyto.internal.results;

import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eclipseutils.ui.copyto.internal.results.messages"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static String ClipboardResultsHandler_Message;
	/**
	 * 
	 */
	public static String ClipboardResultsHandler_Title;
	/**
	 * 
	 */
	public static String ClipboardResultsHandler_Toggle;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

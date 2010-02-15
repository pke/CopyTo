package eclipseutils.ui.copyto.internal.commands;

import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eclipseutils.ui.copyto.internal.commands.messages"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static String CopyToHandler_CollectTask;
	/**
	 * 
	 */
	public static String CopyToHandler_CopyError;
	/**
	 * 
	 */
	public static String CopyToHandler_DropDown_Tooltip_Configure;
	/**
	 * 
	 */
	public static String CopyToHandler_DropDown_Tooltip_CopyTo;
	/**
	 * 
	 */
	public static String CopyToHandler_DropDown_Tooltip_SelectTarget;
	/**
	 * 
	 */
	public static String CopyToHandler_JobName;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

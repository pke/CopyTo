package eclipseutils.ui.copyto.internal.impl;

import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "eclipseutils.ui.copyto.internal.impl.messages"; //$NON-NLS-1$
	/**
	 * 
	 */
	public static String CopyServiceImpl_MimeTypeVar;
	/**
	 * 
	 */
	public static String CopyServiceImpl_SourceVar;
	/**
	 * 
	 */
	public static String CopyServiceImpl_TextVar;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

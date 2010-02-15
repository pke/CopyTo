package eclipseutils.ui.copyto.internal;

import org.osgi.service.log.LogService;

import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;

/**
 * Easier logging to the OGSi LogService or the standard output/error streams.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public final class LogHelper {

	public static void debug(final String message, final Object... args) {
		log(LogService.LOG_DEBUG, null, message, args);
	}

	public static void info(final String message, final Object... args) {
		log(LogService.LOG_INFO, null, message, args);
	}

	public static void warn(final String message, final Object... args) {
		log(LogService.LOG_WARNING, null, message, args);
	}

	public static void error(final Throwable t, final String message,
			final Object... args) {
		log(LogService.LOG_ERROR, t, message, args);
	}

	public static void log(final int level, final Throwable t,
			final String message, final Object... args) {
		final String text = String.format(message, args);
		Trackers.run(LogService.class, new SimpleServiceRunnable<LogService>() {

			@Override
			protected void doRun(final LogService service) {
				service.log(level, text, t);
			}

			@Override
			protected void doRun() {
				if (level == LogService.LOG_ERROR) {
					if (t != null) {
						t.printStackTrace(System.err);
					} else {
						System.err.println(text);
					}
				} else {
					System.out.println(text);
				}
			}
		});
	}

	private LogHelper() {
	}
}

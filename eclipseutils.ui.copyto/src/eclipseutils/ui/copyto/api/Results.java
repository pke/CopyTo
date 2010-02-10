package eclipseutils.ui.copyto.api;

import java.util.Collection;

/**
 * A set of results of a copy action.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface Results {

	/**
	 * @return the results that could be copied successfully.
	 */
	Collection<Result> getSuccesses();

	/**
	 * @return the results that could not be copied successfully.
	 */
	Collection<Result> getFailures();

}

package copyto.core;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A set of results of a copy action.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class Results {

	private Collection<Result> successes;
	private Collection<Result> failures;
	private final Target target;

	/**
	 * @param target
	 */
	public Results(final Target target) {
		this.target = target;
	}

	/**
	 * @param result
	 */
	public void add(final Result result) {
		if (result.getStatus().isOK()) {
			getSuccesses().add(result);
		} else {
			if (result.getStatus().getException() != null) {
				result.getStatus().getException().printStackTrace(System.err);
			}
			getFailures().add(result);
		}
	}

	/**
	 * @return the results that could be copied successfully.
	 */
	public Collection<Result> getSuccesses() {
		if (successes == null) {
			successes = new ArrayList<Result>();
		}
		return successes;
	}

	/**
	 * @return the results that could not be copied successfully.
	 */
	public Collection<Result> getFailures() {
		if (failures == null) {
			failures = new ArrayList<Result>();
		}
		return failures;
	}

	/**
	 * @return the target this result is for
	 */
	public Target getTarget() {
		return target;
	}
}

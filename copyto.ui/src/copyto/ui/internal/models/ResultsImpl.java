package copyto.ui.internal.models;

import java.util.ArrayList;
import java.util.Collection;

import copyto.core.Result;
import copyto.core.Results;

/**
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ResultsImpl implements Results {

	private Collection<Result> successes;
	private Collection<Result> failures;
	private final TargetModel target;

	/**
	 * @param target
	 */
	public ResultsImpl(final TargetModel target) {
		this.target = target;
	}

	/**
	 * @param result
	 */
	public void add(final Result result) {
		if (result.getStatus().isOK() && result.getLocation() != null) {
			getSuccesses().add(result);
		} else {
			if (result.getStatus().getException() != null) {
				result.getStatus().getException().printStackTrace(System.err);
			}
			getFailures().add(result);
		}
	}

	public Collection<Result> getSuccesses() {
		if (successes == null) {
			successes = new ArrayList<Result>();
		}
		return successes;
	}

	public Collection<Result> getFailures() {
		if (failures == null) {
			failures = new ArrayList<Result>();
		}
		return failures;
	}

	public TargetModel getTarget() {
		return target;
	}
}

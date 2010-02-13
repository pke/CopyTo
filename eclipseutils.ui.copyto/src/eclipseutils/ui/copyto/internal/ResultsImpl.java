package eclipseutils.ui.copyto.internal;

import java.util.ArrayList;
import java.util.Collection;

import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.api.Results;

public class ResultsImpl implements Results {

	private Collection<Result> successes;
	private Collection<Result> failures;

	public void add(final Result result) {
		if (result.getStatus().isOK() && result.getLocation() != null) {
			getSuccesses().add(result);
		} else {
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

}

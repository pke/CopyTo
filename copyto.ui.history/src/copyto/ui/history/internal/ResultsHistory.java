package copyto.ui.history.internal;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Shell;

import copyto.core.Results;
import copyto.ui.UIResultHandler;

public class ResultsHistory extends ArrayList<Results> implements UIResultHandler {
	private static final long serialVersionUID = 1L;

	public void handleResults(Results result, Shell shell) {
		add(result);
	}

}

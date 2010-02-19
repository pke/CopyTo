package eclipseutils.ui.copyto.history.internal;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Shell;

import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.UIResultHandler;

public class ResultsHistory extends ArrayList<Results> implements UIResultHandler {
	private static final long serialVersionUID = 1L;

	public void handleResults(Results result, Shell shell) {
		add(result);
	}

}

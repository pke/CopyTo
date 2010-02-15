package eclipseutils.ui.copyto.history.internal;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.window.IShellProvider;

import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.UIResultHandler;

public class ResultsHistory extends WritableList implements UIResultHandler {
	
	public void handleResults(Results result, IShellProvider shellProvider) {
		add(result);
	}

}

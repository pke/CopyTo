package eclipseutils.ui.copyto.internal;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.State;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

public class CopyToDropDownHandler extends AbstractHandler implements
		IElementUpdater {

	public static final String COMMAND_ID = "eclipseutils.ui.copyto.dropdown"; //$NON-NLS-1$

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		State state = commandService.getCommand(CopyToHandler.COMMAND_ID)
				.getState("lastId");

		try {
			ParameterizedCommand parameterizedCommand = LastCommandState.getId(
					commandService, state);
			return parameterizedCommand.executeWithChecks(event.getTrigger(),
					event.getApplicationContext());
		} catch (Exception e) {
		}
		Event triggerEvent = (Event) event.getTrigger();
		triggerEvent.detail = 4; // Drop-down
		Rectangle point = ((ToolItem) triggerEvent.widget).getBounds();
		triggerEvent.x = point.x;
		triggerEvent.y = point.height;
		triggerEvent.widget.notifyListeners(SWT.Selection, triggerEvent);
		return null;
	}

	@SuppressWarnings("rawtypes")
	public void updateElement(UIElement element, Map parameters) {
		ICommandService commandService = (ICommandService) element
				.getServiceLocator().getService(ICommandService.class);
		Command command = commandService.getCommand(CopyToHandler.COMMAND_ID);
		State lastIdState = command.getState("lastId");
		String label = LastCommandState.getLabel(lastIdState);
		if (!"".equals(label)) {
			String text = NLS.bind("CopyTo {0}", label);
			element.setTooltip(text);

			/*ParameterizedCommand parameterizedCommand = LastCommandState.getId(
					commandService, lastIdState);
			if (parameterizedCommand != null) {
				CopyToHandler.loadFavIcon(element,
						(String) parameterizedCommand.getParameterMap().get(
								"url"));
			}*/
		} else {
			element.setTooltip("Select a CopyTo target from the dropdown");
		}
	}

}

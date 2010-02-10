/*******************************************************************************
 * Copyright (c) 2010 Philipp Kursawe.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   Philipp Kursawe (phil.kursawe@gmail.com) - initial API and implementation
 ******************************************************************************/
package eclipseutils.ui.copyto.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IStateListener;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.State;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.IProgressConstants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Result;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.ResultsHandler;
import eclipseutils.ui.copyto.internal.dialogs.RequestParamsDialog;
import eclipseutils.ui.copyto.internal.results.ClipboardResultsHandler;

/**
 * 
 * There are 2 types of handlers: 1. ElementHandler 2. TextEditorHandler
 * 
 * @author <a href="mailto:kursawe@topsystem.de">Philipp Kursawe</a>
 * @since 1.0
 */
public class CopyToHandler extends AbstractHandler implements IStateListener,
		IElementUpdater {

	public static final String COMMAND_ID = "eclipseutils.ui.copyto"; //$NON-NLS-1$
	public static final String COMMAND_TARGET_PARAM = "targets"; //$NON-NLS-1$

	private final IAdapterManager adapterManager = Platform.getAdapterManager();
	private final ServiceTracker resultsHandlerTracker = new ServiceTracker(
			FrameworkUtil.getBundle(CopyToHandler.class).getBundleContext(),
			ResultsHandler.class.getName(), null) {
		{
			open();
		}
	};

	@Override
	public void dispose() {
		resultsHandlerTracker.close();
		super.dispose();
	}

	class EventHttpCopyHandler extends HttpCopyToHandler {

		private final HttpMethod method;
		private final String id;

		public EventHttpCopyHandler(String id, String url, String method) {
			this.id = id;
			this.method = new PostMethod(url);
		}

		@Override
		protected HttpMethod getMethod() {
			return this.method;
		}

		public String getId() {
			return this.id;
		}

		public String getHost() {
			try {
				return getMethod().getURI().toString();
			} catch (URIException e) {
				return "Unknown";
			}
		}
	}

	/**
	 * If CTRL key hold down 1. First collect all Copyable 2. Group them by
	 * mime-type 3. Show wizard page for each mime-type (resolve vars before
	 * displaying page) 4. Upon "Finish click", send to server -> report
	 * progress
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection[] = { HandlerUtil
				.getActiveMenuSelection(event) };
		if (selection[0] == null) {
			selection[0] = HandlerUtil.getCurrentSelectionChecked(event);
		}

		if (selection[0].isEmpty()) {
			return null;
		}

		final Map<String, String> params = new HashMap<String, String>();
		String urlParams = event.getParameter("url");
		int indexOf = urlParams.indexOf('?');
		final String url = urlParams.substring(0, indexOf);
		urlParams = urlParams.substring(indexOf + 1);
		final String[] pairs = urlParams.split("&"); //$NON-NLS-1$
		for (int i = 0; i < pairs.length; ++i) {
			final String[] keyValue = pairs[i].split("="); //$NON-NLS-1$
			params.put(keyValue[0], keyValue.length == 1 ? "" : keyValue[1]);
		}

		final IPreferenceStore prefs = new ScopedPreferenceStore(
				new InstanceScope(), FrameworkUtil.getBundle(getClass())
						.getSymbolicName());
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		final IWorkbenchWindow workbenchWindow = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		final String itemText = getTriggerItemText(event.getTrigger());

		final Results results[] = { null };

		final Action runAction = new Action("Copy to clipboard") {
			@Override
			public void run() {
				new ClipboardResultsHandler().handleResults(results[0],
						workbenchWindow);
			}
		};
		Job job = new Job("Gathering copyable items") {

			final List<Result> successes = new ArrayList<Result>();
			final List<Result> failures = new ArrayList<Result>();

			@Override
			public IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(
						"Gathering copyable information from selection",
						IProgressMonitor.UNKNOWN);

				final Map<String, Copyable> items = new HashMap<String, Copyable>();

				if (selection[0] instanceof IStructuredSelection) {
					final IStructuredSelection ss = (IStructuredSelection) selection[0];
					final Iterator<?> it = ss.iterator();
					while (it.hasNext()) {
						final Object item = it.next();

						Copyable copyable = (Copyable) adapterManager
								.loadAdapter(item, Copyable.class.getName());
						if (copyable == null) {
							final IResource resource = (IResource) adapterManager
									.loadAdapter(item, IResource.class
											.getName());
							if (resource != null) {
								copyable = (Copyable) adapterManager
										.loadAdapter(resource, Copyable.class
												.getName());
							}
						}

						if (copyable != null) {
							items.put(copyable.getMimeType(), copyable);
						}
					}
				} else if (selection[0] instanceof ITextSelection) {
					ITextSelection textSelection = (ITextSelection) selection[0];
					Copyable copyable = (Copyable) adapterManager.loadAdapter(
							editor, Copyable.class.getName());
					if (null == copyable) {
						copyable = new TextSelectionCopyable(textSelection);
					}
					items.put(copyable.getMimeType(), copyable);
				}

				monitor.beginTask("Copying...", items.size());
				boolean showDialog = showDialog(event.getTrigger());
				final EventHttpCopyHandler target = new EventHttpCopyHandler(
						event.getParameter("id"), url, event
								.getParameter("method"));

				for (Entry<String, Copyable> item : items.entrySet()) {
					Map<String, String> copyParams = resolveParams(item
							.getValue(), params);
					if (showDialog) {
						Image image = null;
						try {
							URL url = new URL(target.getHost() + "/favicon.ico");
							image = new Image(Display.getDefault(), url
									.openStream());
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						final RequestParamsDialog dialog = new RequestParamsDialog(
								workbenchWindow.getShell(), event
										.getParameter("id"), copyParams);
						dialog.setTitleImage(image);
						Display.getDefault().syncExec(new Runnable() {

							public void run() {
								dialog.open();
							}
						});
						if (Window.OK != dialog.getReturnCode()) {
							continue;
						}
					}
					final Result result = target.copy(item.getValue(),
							copyParams, monitor);
					if (result.getStatus().isOK()
							&& result.getLocation() != null) {
						successes.add(result);
					} else {
						failures.add(result);
					}
					monitor.worked(1);
				}

				if (!successes.isEmpty() || !failures.isEmpty()) {
					results[0] = new Results() {

						public Collection<Result> getFailures() {
							return failures;
						}

						public Collection<Result> getSuccesses() {
							return successes;
						}

					};
					Object[] services = resultsHandlerTracker.getServices();
					if (services != null) {
						for (Object service : services) {
							try {
								((ResultsHandler) service).handleResults(
										results[0], workbenchWindow);
							} catch (Throwable t) {
							}
						}
					}
				}
				monitor.done();

				if (!failures.isEmpty()) {
					setProperty(IProgressConstants.KEEP_PROPERTY, true);
					setProperty(
							IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY,
							true);
					return new Status(IStatus.ERROR,
							HttpCopyToHandler.symbolicName, NLS.bind(
									"Copying to {0} had {1} errors.", target
											.getHost(), failures.size()));
				}

				if (successes.isEmpty()) {
					return Status.CANCEL_STATUS;
				}

				// Returns a CommandStateProxy instead of the real LastIdState
				// class, so we cannot use it directly here.
				State lastIdState = event.getCommand().getState("lastId");
				setState(lastIdState, ParameterizedCommand.generateCommand(
						event.getCommand(), event.getParameters()), itemText);

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						runAction.run();
						// TODO: If user chooses to cancel, we want to preserve
						// the entry
						// in the progress view:
						// setProperty(IProgressConstants.KEEP_PROPERTY, true);
					}
				});

				return new Status(IStatus.OK, HttpCopyToHandler.symbolicName,
						NLS.bind("Selection copied successfully to {0}.",
								target.getHost()));
			}
		};
		job.setProperty(IProgressConstants.ACTION_PROPERTY, runAction);
		job.schedule();

		return null;
	}

	/**
	 * Must be called from the UI-Thread
	 * 
	 * @param trigger
	 * @return
	 */
	private String getTriggerItemText(Object trigger) {
		if (trigger instanceof Event) {
			final Event triggerEvent = (Event) trigger;
			if (triggerEvent.widget instanceof MenuItem) {
				return ((MenuItem) triggerEvent.widget).getText();
			}
		}
		return null;
	}

	/**
	 * @param trigger
	 * @param state
	 * @param id
	 */
	private void setState(final State state,
			final ParameterizedCommand command, String text) {
		if (text != null) {
			LastCommandState.setValue(state, command, text);
		}
	}

	public void handleStateChange(State state, Object oldValue) {
		if (state.getId().equals("lastId")) {
			ICommandService commandService = (ICommandService) PlatformUI
					.getWorkbench().getService(ICommandService.class);
			commandService.refreshElements(CopyToDropDownHandler.COMMAND_ID,
					null);
		}
	}

	private boolean showDialog(Object trigger) {
		if (trigger instanceof Event) {
			final Event triggerEvent = (Event) trigger;
			final int modifier = triggerEvent.stateMask & SWT.MODIFIER_MASK;
			return ((modifier & SWT.CTRL) == SWT.CTRL);
		}
		return false;
	}

	private static Map<String, String> resolveParams(Copyable copyable,
			final Map<String, String> params) {
		final IStringVariableManager variableManager = VariablesPlugin
				.getDefault().getStringVariableManager();
		final String text = copyable.getText();
		final IValueVariable vars[] = {
				variableManager
						.newValueVariable(
								"copyto.source", "Source", true, copyable.getSource().getClass().getName()), //$NON-NLS-1$
				variableManager.newValueVariable(
						"copyto.text", "Text to copy", true, text), //$NON-NLS-1$
				variableManager
						.newValueVariable(
								"copyto.mime-type", "MIME-Type", true, copyable.getMimeType()) }; //$NON-NLS-1$

		// Make sure they are not registered
		variableManager.removeVariables(vars);
		try {
			variableManager.addVariables(vars);
		} catch (final CoreException e) {
		}

		Map<String, String> result = new HashMap<String, String>(params.size());
		for (Entry<String, String> entry : params.entrySet()) {
			try {
				result.put(entry.getKey(), variableManager
						.performStringSubstitution(entry.getValue(), false));
			} catch (final CoreException e) {
			}
		}
		variableManager.removeVariables(vars);
		return result;
	}

	@SuppressWarnings("rawtypes")
	public void updateElement(final UIElement element, Map parameters) {
		// loadFavIcon(element, (String) parameters.get("url"));
	}

	private static void loadFavIcon(final UIElement element, String fullUrl) {
		try {
			final URL url[] = { new URL(fullUrl) };
			url[0] = new URL("http://" + url[0].getHost() + "/favicon.ico");
			new Job("Loading favicon from " + url[0]) {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					final ImageDescriptor imageDesc = ImageDescriptor
							.createFromURL(url[0]);
					if (null != imageDesc.getImageData()) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								element.setIcon(imageDesc);
							}
						});
					}
					return Status.OK_STATUS;
				};
			}.schedule();
		} catch (MalformedURLException e) {
		}
	}
}

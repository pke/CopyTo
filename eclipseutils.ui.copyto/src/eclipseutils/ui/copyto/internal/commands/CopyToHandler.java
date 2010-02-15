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
package eclipseutils.ui.copyto.internal.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.progress.IProgressConstants;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.ui.copyto.api.CopyService;
import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.internal.api.Target;
import eclipseutils.ui.copyto.internal.api.TargetService;
import eclipseutils.ui.copyto.internal.impl.HttpCopyToHandler;
import eclipseutils.ui.copyto.internal.preferences.CopyToPreferencePage;

/**
 * 
 * There are 2 types of handlers: 1. ElementHandler 2. TextEditorHandler
 * 
 * @author <a href="mailto:kursawe@topsystem.de">Philipp Kursawe</a>
 * @since 1.0
 */
public class CopyToHandler extends AbstractHandler implements IElementUpdater {

	/**
	 * 
	 */
	public static final String COMMAND_ID = "eclipseutils.ui.copyto"; //$NON-NLS-1$

	private final IAdapterManager adapterManager = Platform.getAdapterManager();

	class CollectJob extends Job {

		private ISelection selection;
		private final IEditorPart editorPart;
		private final Target target;
		private final Shell shell;

		public CollectJob(final ExecutionEvent event, final Target currentTarget) {
			super(Messages.CopyToHandler_JobName);
			target = currentTarget;
			selection = HandlerUtil.getActiveMenuSelection(event);
			if (selection == null) {
				selection = HandlerUtil.getCurrentSelection(event);
			}
			shell = HandlerUtil.getActiveShell(event);
			editorPart = HandlerUtil.getActiveEditor(event);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			if (selection.isEmpty()) {
				return Status.OK_STATUS;
			}

			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					Messages.CopyToHandler_CollectTask, 100);

			final List<Copyable> items = new ArrayList<Copyable>();

			if (selection instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) selection;
				final Iterator<?> it = ss.iterator();
				while (it.hasNext()) {
					final Object item = it.next();

					Copyable copyable = (Copyable) adapterManager.loadAdapter(
							item, Copyable.class.getName());
					if (copyable == null) {
						final IResource resource = (IResource) adapterManager
								.loadAdapter(item, IResource.class.getName());
						if (resource != null) {
							copyable = (Copyable) adapterManager.loadAdapter(
									resource, Copyable.class.getName());
						}
					}

					if (copyable != null) {
						items.add(copyable);
					}
				}
			} else if (selection instanceof ITextSelection) {
				final ITextSelection textSelection = (ITextSelection) selection;
				Copyable copyable = (Copyable) adapterManager.loadAdapter(
						editorPart, Copyable.class.getName());
				if (null == copyable) {
					copyable = new TextSelectionCopyable(textSelection);
				}
				items.add(copyable);
			}
			final Results results = Trackers.run(CopyService.class,
					new ServiceRunnable<CopyService, Results>() {
						public Results run(final CopyService service) {
							return service.copy(target.getId(), subMonitor
									.newChild(90),
									new SameShellProvider(shell),
									items.toArray(new Copyable[items.size()]));
						}
					});
			if (!results.getFailures().isEmpty()) {
				setProperty(IProgressConstants.KEEP_PROPERTY, true);
				setProperty(
						IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY,
						true);
				return new Status(IStatus.ERROR,
						HttpCopyToHandler.symbolicName,
						NLS.bind(Messages.CopyToHandler_CopyError, target
								.getUrl(), results.getFailures().size()));
			}
			if (results.getSuccesses().isEmpty()) {
				return Status.CANCEL_STATUS;
			}
			return Status.OK_STATUS;
		}
	}

	/**
	 * If CTRL key hold down 1. First collect all Copyable 2. Group them by
	 * mime-type 3. Show wizard page for each mime-type (resolve vars before
	 * displaying page) 4. Upon "Finish click", send to server -> report
	 * progress
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Target currentTarget = (Target) event
				.getObjectParameterForExecution("id"); //$NON-NLS-1$
		if (currentTarget == null) {
			currentTarget = Trackers.run(TargetService.class,
					new ServiceRunnable<TargetService, Target>() {
						public Target run(final TargetService service) {
							Target target = service.getLastSelected();
							if (target == null && service.count() == 1) {
								target = service.findFirst();
							}
							return target;
						}
					});
			if (null == currentTarget) {
				if (event.getTrigger() instanceof Event) {
					final Event triggerEvent = (Event) event.getTrigger();
					if (triggerEvent.widget instanceof ToolItem) {
						triggerEvent.detail = 4; // Drop-down
						final Rectangle point = ((ToolItem) triggerEvent.widget)
								.getBounds();
						triggerEvent.x = point.x;
						triggerEvent.y = point.height;
						triggerEvent.widget.notifyListeners(SWT.Selection,
								triggerEvent);
						return null;
					}
				}
				// In all other cases show the preferences
				CopyToPreferencePage.show(HandlerUtil.getActiveShell(event));
				return null;
			}
		}

		final Job job = new CollectJob(event, currentTarget);
		job.schedule();

		/*
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
										handler.getHost()));
					}
				};
				job.setProperty(IProgressConstants.ACTION_PROPERTY, runAction);
				job.schedule();*/

		return null;
	}

	@SuppressWarnings("unused")
	private boolean showDialog(final Object trigger) {
		if (trigger instanceof Event) {
			final Event triggerEvent = (Event) trigger;
			final int modifier = triggerEvent.stateMask & SWT.MODIFIER_MASK;
			return ((modifier & SWT.CTRL) == SWT.CTRL);
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public void updateElement(final UIElement element, final Map parameters) {
		Trackers.run(TargetService.class,
				new SimpleServiceRunnable<TargetService>() {
					@Override
					public void doRun(final TargetService service) {
						// First display the last selected item as tooltip
						String text = Messages.CopyToHandler_DropDown_Tooltip_SelectTarget;
						Target target = service.getLastSelected();
						// If there is no such item, see how many items there
						// are
						if (target == null) {
							final int count = service.count();
							// If there is none yet, we set a tooltip that will
							// aid the user
							if (count == 0) {
								text = Messages.CopyToHandler_DropDown_Tooltip_Configure;
							} else if (count == 1) {
								target = service.findFirst();
							}
						}
						if (target != null) {
							text = NLS
									.bind(
											Messages.CopyToHandler_DropDown_Tooltip_CopyTo,
											target.getName());
						}
						final String finalText = text;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								element.setTooltip(finalText);
							}
						});
					}
				});
	}
}

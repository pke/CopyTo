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
package copyto.ui.internal.commands;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.progress.IProgressConstants;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.progress.WorkbenchJob;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import copyto.core.Copyable;
import copyto.core.Results;
import copyto.core.Target;
import copyto.core.TargetService;
import copyto.ui.IconProvider;
import copyto.ui.UIResultHandler;
import copyto.ui.WorkbenchResultHandler;
import copyto.ui.internal.Messages;
import copyto.ui.internal.models.TextSelectionCopyable;
import copyto.ui.internal.preferences.CopyToPreferencePage;
import copyto.ui.internal.services.HttpProtocol;
import eclipseutils.core.extensions.ExtensionPoint;

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
	public static final String COMMAND_ID = "copyto.command"; //$NON-NLS-1$

	private final IAdapterManager adapterManager = Platform.getAdapterManager();

	class LabelProvider extends StyledCellLabelProvider implements
			ILabelProvider {

		private final LocalResourceManager resourceManager = new LocalResourceManager(
				JFaceResources.getResources());

		@Override
		public void dispose() {
			resourceManager.dispose();
			super.dispose();
		}

		@Override
		public void update(ViewerCell cell) {
			final StyledString text = new StyledString();
			final IConfigurationElement element = (IConfigurationElement) cell
					.getElement();
			text.append(getText(element), new Styler() {
				@Override
				public void applyStyles(final TextStyle textStyle) {
					textStyle.font = JFaceResources.getFontRegistry().getBold(
							JFaceResources.DIALOG_FONT);
				}
			});
			appendDescription(text, element);
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
			cell.setImage(getImage(element));
			super.update(cell);
		}

		private void appendDescription(StyledString text,
				IConfigurationElement element) {
			String desc = element.getAttribute("description");
			if (desc != null) {
				text.append("\n" + desc, StyledString.DECORATIONS_STYLER);
			}
		}

		public Image getImage(Object element) {
			IConfigurationElement config = (IConfigurationElement) element;
			ImageDescriptor descriptor = null;
			String path = config.getAttribute("icon");
			if (path != null) {
				URL url = FileLocator.find(Platform.getBundle(config
						.getContributor().getName()), new Path(path), null);
				if (url != null) {
					descriptor = ImageDescriptor.createFromURL(url);
				}
			} else {
				String clazz = config.getAttribute("iconProvider");
				if (clazz != null) {
					IconProvider provider;
					try {
						provider = (IconProvider) config
								.createExecutableExtension("iconProvider");
						if (provider != null) {
							descriptor = provider.getIcon();
						}
					} catch (CoreException e) {
					}
				}
			}
			if (descriptor != null) {
				return resourceManager.createImage(descriptor);
			}
			return null;
		}

		public String getText(Object element) {
			IConfigurationElement config = (IConfigurationElement) element;
			return config.getAttribute("name");
		}
	}

	class CollectJob extends Job {

		private ISelection selection;
		private final IEditorPart editorPart;
		private final Target target;
		private Shell shell;
		private IWorkbench workbench;

		public CollectJob(final ExecutionEvent event, final Target currentTarget) {
			super(Messages.CopyToHandler_JobName);
			target = currentTarget;
			selection = HandlerUtil.getActiveMenuSelection(event);
			if (selection == null) {
				selection = HandlerUtil.getCurrentSelection(event);
			}
			shell = HandlerUtil.getActiveShell(event);
			workbench = HandlerUtil.getActiveWorkbenchWindow(event)
					.getWorkbench();
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
						copyable = getResourceCopyable(item);
					}

					if (copyable != null) {
						items.add(copyable);
					}
				}
			} else if (selection instanceof ITextSelection) {
				final ITextSelection textSelection = (ITextSelection) selection;
				Copyable copyable;
				if (textSelection.getLength() > 0) {
					copyable = new TextSelectionCopyable(textSelection);
				} else {
					copyable = (Copyable) adapterManager.loadAdapter(
							editorPart, Copyable.class.getName());
				}
				if (null == copyable) {
					final IEditorInput editorInput = editorPart
							.getEditorInput();
					copyable = getResourceCopyable(editorInput);
				}
				if (copyable != null) {
					items.add(copyable);
				}
			}
			Trackers.run(TargetService.class,
					new SimpleServiceRunnable<TargetService>() {
						@Override
						protected void doRun(TargetService service) {
							service.setLastSelected(target.getId());
						}
					});
			final Results results = target.copy(subMonitor, items
					.toArray(new Copyable[items.size()]));
			if (!results.getFailures().isEmpty()) {
				setProperty(IProgressConstants.KEEP_PROPERTY, true);
				setProperty(
						IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY,
						true);
				return new Status(IStatus.ERROR, HttpProtocol.symbolicName, NLS
						.bind(Messages.CopyToHandler_CopyError, target
								.getName(), results.getFailures().size()));
			}
			if (results.getSuccesses().isEmpty()) {
				return Status.CANCEL_STATUS;
			}

			ExtensionPoint ep = new ExtensionPoint("copyto.core.resultHandlers");
			IEvaluationContext context = new EvaluationContext(null, results);
			final Collection<IConfigurationElement> elements = ep.getElements(
					context, false);

			if (elements.size() == 1) {
				IConfigurationElement config = elements.iterator().next();
				runAction(config, results);
			} else {

				WorkbenchJob job = new WorkbenchJob("Choose action") {
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						final ILabelProvider labelProvider = new LabelProvider();
						ListDialog dialog = new ListDialog(shell) {

							@Override
							protected int getTableStyle() {
								return SWT.NO_SCROLL | SWT.BORDER
										| SWT.V_SCROLL;
							}

							@Override
							protected Control createDialogArea(
									Composite container) {
								Control control = super
										.createDialogArea(container);
								Table table = getTableViewer().getTable();
								table.addListener(SWT.MeasureItem,
										new Listener() {

											public void handleEvent(Event event) {
												event.height = 28;
											}
										});
								return control;
							}
						};
						dialog.setTitle("Select action");
						dialog.setMessage(NLS
								.bind(
										"Selection was copied successfully to \"{0}\".\nWhat do you want to do now?",
										target.getName()));
						dialog.setHelpAvailable(false);
						dialog.setLabelProvider(labelProvider);
						dialog.setContentProvider(ArrayContentProvider
								.getInstance());
						dialog.setInput(elements);
						if (Window.OK == dialog.open()) {
							Object[] dialogResults = dialog.getResult();
							if (dialogResults != null) {
								for (Object result : dialogResults) {
									runAction((IConfigurationElement) result,
											results);
								}
							}
						}
						return Status.OK_STATUS;
					}
				};
				job.setSystem(true);
				job.schedule();
			}
			return Status.OK_STATUS;
		}

		private void runAction(IConfigurationElement config,
				final Results results) {
			try {
				final UIResultHandler handler = (UIResultHandler) config
						.createExecutableExtension("class");
				UIJob handlerJob = new UIJob(config.getAttribute("name")) {
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						if (handler instanceof WorkbenchResultHandler) {
							((WorkbenchResultHandler) handler).init(workbench);
						}
						handler.handleResults(results, shell);
						return Status.OK_STATUS;
					}
				};
				handlerJob.setSystem(true);
				handlerJob.schedule();
			} catch (CoreException e) {

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

	/**
	 * Tries to adapt the item first to IResource and then to Copyable.
	 * 
	 * @param item
	 * @return
	 */
	private Copyable getResourceCopyable(final Object item) {
		final IResource resource = (IResource) adapterManager.loadAdapter(item,
				IResource.class.getName());
		if (resource != null) {
			return (Copyable) adapterManager.loadAdapter(resource,
					Copyable.class.getName());
		}
		return null;
	}
}

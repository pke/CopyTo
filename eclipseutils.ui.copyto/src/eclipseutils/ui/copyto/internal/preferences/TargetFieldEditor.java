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
package eclipseutils.ui.copyto.internal.preferences;

import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import osgiutils.services.DefaultCollectionServiceRunnable;
import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.jface.preferences.AbstractTableViewerFieldEditor;
import eclipseutils.ui.copyto.internal.api.Target;
import eclipseutils.ui.copyto.internal.api.TargetService;

class TargetFieldEditor extends AbstractTableViewerFieldEditor<Target> {

	TargetFieldEditor(final String preferencePath, final String labelText,
			final Composite parent) {
		super(preferencePath, labelText, parent, 0);
	}

	@Override
	protected Target createItem(final Shell shell) {
		final Target target = new Target();
		if (new EditDialog(shell, target, getItems()).open() == Window.OK) {
			return target;
		}
		return null;
	}

	@Override
	protected void editItem(final Shell shell, final Target item) {
		new EditDialog(shell, item, getItems()).open();
	}

	@Override
	protected void createCustomButtons(final Composite parent) {
		// createTestButton(parent);
		createCopyButton(parent);
		createPasteButton(parent);
	}

	private void createPasteButton(final Composite parent) {
		final Button pasteButton = createPushButton(parent, "Paste");
		pasteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Clipboard clipboard = new Clipboard(Display.getDefault());
				try {
					final String base64 = (String) clipboard
							.getContents(TextTransfer.getInstance());
					final Target item = Target.valueOf(base64);
					if (item != null) {
						add(item);
					}
				} catch (final Exception ex) {
					MessageDialog
							.openError(pasteButton.getShell(),
									"Error pasting CopyTo target",
									"The clipboard does not contain a valid CopyTo target for pasting");
					ex.printStackTrace();
				} finally {
					clipboard.dispose();
				}
			}
		});
	}

	private void createCopyButton(final Composite parent) {
		final Button copyButton = createPushButton(parent, "Copy");
		enableWithSelection(copyButton, SWT.SINGLE);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				visitViewerSelection("Copying...", new Visitor<Target>() {
					public void visit(final Target item,
							final IProgressMonitor monitor) {
						final String base64 = item.toBase64();
						Display.getDefault().asyncExec(new Runnable() {

							public void run() {
								final Clipboard clipboard = new Clipboard(
										Display.getDefault());
								try {
									clipboard.setContents(
											new Object[] { base64 },
											new Transfer[] { TextTransfer
													.getInstance() });
								} finally {
									clipboard.dispose();
								}
							}
						});
					}
				});
			}
		});
	}

	private void createTestButton(final Composite parent) {
		final Button testButton = createPushButton(parent, "Test");
		testButton.setToolTipText("Test the connectivity to the selected URL");
		enableWithSelection(testButton, SWT.SINGLE | SWT.MULTI);
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				testButton.setEnabled(false);
				visitViewerSelection("Testing connection",
						new Visitor<Target>() {
							public void visit(final Target target,
									final IProgressMonitor monitor) {
								monitor.beginTask(NLS.bind("Testing {0}: ",
										target.getName(), target.getUrl()),
										IProgressMonitor.UNKNOWN);
								for (int i = 0; i < 10; ++i) {
									target.testConnection();
								}
							}
						});
				testButton.setEnabled(true);
			}
		});
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { "name", "url" };
	}

	@Override
	protected String[] getColumnLabels() {
		return new String[] { "Name", "URL" };
	}

	@Override
	protected EditingSupport createEditingSupport(final String name,
			final TableViewer viewer, final DataBindingContext context) {
		/*if ("label".equals(name)) {
			IValueProperty cellEditorControlText = CellEditorProperties
					.control().value(WidgetProperties.text());
			return ObservableValueEditingSupport.create(viewer, context,
					new TextCellEditor(viewer.getTable()),
					cellEditorControlText, BeanProperties.value(Target.class,
							"label"));
		}*/
		return super.createEditingSupport(name, viewer, context);
	}

	@Override
	protected Collection<Target> loadItems() {
		return Trackers.run(TargetService.class,
				new DefaultCollectionServiceRunnable<TargetService, Target>() {
					public Collection<Target> run(final TargetService service) {
						return service.findAll();
					}
				});
	}

	@Override
	protected void doLoadDefault() {
		doLoad();
	}

	@Override
	protected void doStore() {
		Trackers.run(TargetService.class,
				new SimpleServiceRunnable<TargetService>() {
					@Override
					public void doRun(final TargetService service) {
						service.save(getItems());
					}
				});
	}
}
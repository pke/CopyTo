package copyto.ui.internal.preferences;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
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
import osgiutils.services.Trackers;
import copyto.core.Target;
import copyto.core.TargetFactories;
import copyto.core.TargetFactoryDescriptor;
import copyto.core.models.AbstractTargetModel;
import copyto.ui.internal.Messages;
import eclipseutils.jface.databinding.TableEditor;


class TargetEditor extends TableEditor<Target> {

	protected TargetEditor(Composite parent, Collection<Target> items, int flags) {
		super(parent, items, flags);
	}

	@Override
	protected String[] getColumnNames() {
		return new String[] { "name", "url" }; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected String[] getColumnLabels() {
		return new String[] { Messages.TargetFieldEditor_NameColumn,
				Messages.TargetFieldEditor_URLColumn };
	}

	@Override
	protected Target createItem(Shell shell) {
		Collection<TargetFactoryDescriptor> descs = Trackers.run(TargetFactories.class, new DefaultCollectionServiceRunnable<TargetFactories, TargetFactoryDescriptor>() {
			public Collection<TargetFactoryDescriptor> run(TargetFactories service) {
				return service.findAll();
			}
		});
		if (descs.isEmpty()) {
			return null;
		}
		if (descs.size() == 1) {
			TargetFactoryDescriptor desc = descs.iterator().next();
			Target target = desc.getFactory().createTarget();
			if (new EditDialog(shell, target, getItems()).open() == Window.OK) {
				return target;
			}
		} else {
			return null;
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
		final Button pasteButton = createPushButton(parent,
				Messages.TargetFieldEditor_Paste);
		pasteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Clipboard clipboard = new Clipboard(Display.getDefault());
				try {
					final String base64 = (String) clipboard
							.getContents(TextTransfer.getInstance());
					final AbstractTargetModel item = AbstractTargetModel.valueOf(base64);
					if (item != null) {
						add(item);
					}
				} catch (final Exception ex) {
					MessageDialog.openError(pasteButton.getShell(),
							Messages.TargetFieldEditor_PasteError_Title,
							Messages.TargetFieldEditor_PasteError_Message);
					ex.printStackTrace();
				} finally {
					clipboard.dispose();
				}
			}
		});
	}

	private void createCopyButton(final Composite parent) {
		final Button copyButton = createPushButton(parent,
				Messages.TargetFieldEditor_Copy);
		enableWithSelection(copyButton, SWT.SINGLE);
		copyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				visitViewerSelection(Messages.TargetFieldEditor_CopyJob,
						new Visitor<Target>() {
							public void visit(final Target item,
									final IProgressMonitor monitor) {
								final String base64 = ((AbstractTargetModel)item).toBase64();
								Display.getDefault().asyncExec(new Runnable() {

									public void run() {
										final Clipboard clipboard = new Clipboard(
												Display.getDefault());
										try {
											clipboard
													.setContents(
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

	@SuppressWarnings("unused")
	private void createTestButton(final Composite parent) {
		final Button testButton = createPushButton(parent,
				Messages.TargetFieldEditor_Test);
		testButton.setToolTipText(Messages.TargetFieldEditor_TestDesc);
		enableWithSelection(testButton, SWT.SINGLE | SWT.MULTI);
		testButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				testButton.setEnabled(false);
				visitViewerSelection(Messages.TargetFieldEditor_TestJob,
						new Visitor<Target>() {
							public void visit(final Target target,
									final IProgressMonitor monitor) {
								monitor.beginTask(
										NLS.bind(
												Messages.TargetFieldEditor_TestProgress,
												target.getName(), null),
										IProgressMonitor.UNKNOWN);
								for (int i = 0; i < 10; ++i) {
									//((AbstractTargetModel)target).testConnection();
								}
							}
						});
				testButton.setEnabled(true);
			}
		});
	}		
}
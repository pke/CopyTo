package eclipseutils.ui.copyto.win32.miranda.internal;

import java.net.URL;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.osgi.framework.Bundle;

class FilteredParticipantsSelectionDialog extends FilteredItemsSelectionDialog {

	private static final String SETTINGS = FilteredParticipantsSelectionDialog.class
			.getCanonicalName();
	private MirandaIRC mirandaIRC;

	public FilteredParticipantsSelectionDialog(Shell shell,
			MirandaIRC mirandaIRC) {
		super(shell);
		this.mirandaIRC = mirandaIRC;
		setDetailsLabelProvider(getDetailsLabelProvider());
		setListLabelProvider(getListLabelProvider());
		setImage(shell);
		setInitialPattern("?");
		setMessage("Select the user that you want to message with the link:");
		setTitle("Paste to IRC channel");
	}

	private void setImage(Shell shell) {
		Bundle bundle = Platform.getBundle("eclipseutils.ui.copyto");
		if (bundle != null) {
			URL url = FileLocator.find(bundle, new Path(
					"$nl$/icons/e16/copyto.png"), null);
			if (url != null) {
				ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
				final Image image = imageDesc.createImage();
				shell.addDisposeListener(new DisposeListener() {
					public void widgetDisposed(DisposeEvent e) {
						image.dispose();
					}
				});
				setImage(image);
			}
		}
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		/*
		 * Composite client = new Composite(parent, SWT.NULL);
		 * GridLayoutFactory.fillDefaults().applyTo(client); Label label = new
		 * Label(client, SWT.LEFT); label.setText("Paste to channel" + ":");
		 * 
		 * return client;
		 */
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		DialogSettings workbenchSettings = new DialogSettings("Workbench");
		IDialogSettings settings = workbenchSettings.getSection(SETTINGS);

		if (settings == null) {
			settings = workbenchSettings.addNewSection(SETTINGS);
		}

		return settings;
	}

	@Override
	protected IStatus validateItem(Object item) {
		return Status.OK_STATUS;
	}

	class ParticipantsFilter extends ItemsFilter {

		@Override
		public boolean matchItem(Object item) {
			return matches(((Participant) item).getUser());
		}

		@Override
		public boolean isConsistentItem(Object item) {
			return true;
		}

	}

	@Override
	protected ItemsFilter createFilter() {
		return new ParticipantsFilter();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Comparator getItemsComparator() {
		return new Comparator<Participant>() {
			public int compare(Participant person1, Participant person2) {
				Collator collator = Collator.getInstance();
				return collator.compare(person1.getUser(), person2.getUser());
			}
		};
	}

	@Override
	protected void fillContentProvider(final AbstractContentProvider contentProvider,
			final ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException {
		mirandaIRC.visitTabs(new Visitor<SESSION_INFO>() {

			public boolean visit(SESSION_INFO item) {
				for (String user : item.getUserNames()) {
					contentProvider.add(new Participant(mirandaIRC, item.getName(), user),
							itemsFilter);
				}
				return true;
			}

		});
	}

	@Override
	public String getElementName(Object item) {
		return ((Participant) item).getUser();
	}

	private class ParticpantLabelProvider extends LabelProvider implements
			IStyledLabelProvider {

		public StyledString getStyledText(Object element) {
			StyledString result = new StyledString();
			if (element instanceof Participant) {
				Participant participant = (Participant) element;
				result.append(participant.getUser());
				result.append(" in " + participant.getChannel(),
						StyledString.QUALIFIER_STYLER);
			}
			return result;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof Participant) {
				Participant participant = (Participant) element;
				return String.format("%s #%s", participant.getUser(),
						participant.getChannel());
			}
			return super.getText(element);
		}
	}

	private ILabelProvider getListLabelProvider() {
		return new ParticpantLabelProvider();
	}

	private ILabelProvider getDetailsLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Participant) {
					Participant participant = (Participant) element;
					return String
							.format("Message URL to %s in channel %s",
									participant.getUser(), participant
											.getChannel());
				}
				return super.getText(element);
			}
		};
	}

}
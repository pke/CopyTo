package copyto.paste.chat.ui;

import java.text.Collator;
import java.util.Comparator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import copyto.paste.chat.core.ChannelUser;
import copyto.paste.chat.core.ChatUser;

public abstract class FilteredParticipantsSelectionDialog extends
		FilteredItemsSelectionDialog {

	private static final String SETTINGS = FilteredParticipantsSelectionDialog.class
			.getCanonicalName();

	public FilteredParticipantsSelectionDialog(Shell shell) {
		super(shell);
		setDetailsLabelProvider(getDetailsLabelProvider());
		setListLabelProvider(getListLabelProvider());
		setInitialPattern("?");
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
		/*Composite client = new Composite(parent, SWT.NULL);
		GridLayoutFactory.fillDefaults().applyTo(client);
		Label label = new Label(client, SWT.LEFT);
		label.setText("Paste to channel" + ":");*/
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
			return matches(((ChatUser) item).getName());
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
		return new Comparator<ChatUser>() {
			public int compare(ChatUser person1, ChatUser person2) {
				if (person1 instanceof ChannelUser) {
					return -1;
				} else if (person2 instanceof ChannelUser) {
					return 1;
				}
				Collator collator = Collator.getInstance();
				return collator.compare(person1.getName(), person2.getName());
			}
		};
	}

	@Override
	public String getElementName(Object item) {
		return ((ChatUser) item).getName();
	}

	private class ParticpantLabelProvider extends LabelProvider implements
			IStyledLabelProvider {

		public StyledString getStyledText(Object element) {
			StyledString result = new StyledString();
			if (element instanceof ChatUser) {
				ChatUser participant = (ChatUser) element;
				if (participant instanceof ChannelUser) {
					result.append("All users", new Styler() {
						@Override
						public void applyStyles(final TextStyle textStyle) {
							textStyle.font = JFaceResources.getFontRegistry().getBold(
									JFaceResources.DIALOG_FONT);
						}
					});
				} else {
					result.append(participant.getName());
				}
				result.append(" in " + participant.getRoom().getName(),
						StyledString.QUALIFIER_STYLER);
			}
			return result;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof ChatUser) {
				ChatUser participant = (ChatUser) element;
				return String.format("%s #%s", participant.getName(),
						participant.getRoom().getName());
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
				if (element instanceof ChatUser) {
					ChatUser participant = (ChatUser) element;
					return String.format("Message URL to %s in channel %s",
							participant.getName(), participant.getRoom()
									.getName());
				}
				return super.getText(element);
			}
		};
	}

}
package eclipseutils.ui.copyto.internal.preferences;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import eclipseutils.ui.copyto.internal.Target;
import eclipseutils.ui.copyto.internal.jface.databinding.Builder;
import eclipseutils.ui.copyto.internal.jface.databinding.BuiltDialog;
import eclipseutils.ui.copyto.internal.jface.databinding.StandardBuilder;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.CompoundValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.ListValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.NotEmptyValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.NotValidator;
import eclipseutils.ui.copyto.internal.jface.databinding.validators.URLValidator;

/**
 * TODO: Add validation of URL that it contains at least ${copyto.text} Make
 * sure the preferences are initialized.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
class EditDialog extends BuiltDialog {

	private final Target target;
	private final HashSet<String> existingItems;

	public EditDialog(final Shell parentShell, final Target target,
			final Collection<Target> existingItems) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL | SWT.RESIZE | getDefaultOrientation());
		this.target = target;
		this.existingItems = new HashSet<String>();
		for (final Target t : existingItems) {
			if (!t.getName().equals(target.getName())) {
				this.existingItems.add(t.getName());
			}
		}
		setHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Control control = super.createDialogArea(parent);
		getShell().setText("CopyTo Target");
		setTitle("Target informations");
		setMessage("Enter the informations for this CopyTo target below");
		return control;
	}

	public Builder createBuilder(final Composite parent) {
		final IValidator labelValidator = new CompoundValidator(
				NotEmptyValidator.getInstance(true),
				new NotValidator(
						new ListValidator(existingItems),
						"A Target with that name already exists. You should choose another name.",
						IStatus.WARNING));
		return new StandardBuilder(parent, target,
				UpdateValueStrategy.POLICY_CONVERT).field("name",
				labelValidator).newLine().field("url",
				URLValidator.getInstance()).newLine().field("visible");
	}
}
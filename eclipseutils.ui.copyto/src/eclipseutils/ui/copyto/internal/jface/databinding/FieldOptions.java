package eclipseutils.ui.copyto.internal.jface.databinding;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;

public class FieldOptions {
	private static final int defaultProposalAcceptanceStyle = ContentProposalAdapter.PROPOSAL_INSERT;
	private static final String defaultProposalDescription = "Use {0} to open the content assist";
	private static final KeyStroke defaultProposalKeyStroke = KeyStroke
			.getInstance(SWT.MOD1, 32);
	private char[] autoActivationnCharacters;
	private int proposalAcceptanceStyle = defaultProposalAcceptanceStyle;
	private String proposalDescription = defaultProposalDescription;
	private KeyStroke proposalKeyStroke = defaultProposalKeyStroke;
	private IContentProposalProvider proposalProvider;
	private UpdateValueStrategy targetToModel;

	private IValidator validator;

	public FieldOptions(final char[] autoActivationnCharacters) {
		this.autoActivationnCharacters = autoActivationnCharacters;
	}

	public FieldOptions(final IContentProposalProvider proposalProvider) {
		setProposalProvider(proposalProvider);
	}

	public FieldOptions(final IValidator validator) {
		setValidator(validator);
	}

	public FieldOptions(final String proposalKeyStroke) {
		setProposalKeyStroke(proposalKeyStroke);
	}

	public FieldOptions(final UpdateValueStrategy targetToModel) {
		setTargetToModel(targetToModel);
	}

	public char[] getAutoActivationnCharacters() {
		return autoActivationnCharacters;
	}

	public int getProposalAcceptanceStyle() {
		return proposalAcceptanceStyle;
	}

	public String getProposalDescription() {
		return proposalDescription;
	}

	public KeyStroke getProposalKeyStroke() {
		return proposalKeyStroke;
	}

	public IContentProposalProvider getProposalProvider() {
		return proposalProvider;
	}

	public UpdateValueStrategy getTargetToModel() {
		return targetToModel;
	}

	public IValidator getValidator() {
		return validator;
	}

	public FieldOptions setAutoActivationnCharacters(
			final char[] autoActivationnCharacters) {
		this.autoActivationnCharacters = autoActivationnCharacters;
		return this;
	}

	public FieldOptions setProposalAcceptanceStyle(
			final int proposalAcceptanceStyle) {
		this.proposalAcceptanceStyle = proposalAcceptanceStyle;
		return this;
	}

	public FieldOptions setProposalDescription(final String proposalDescription) {
		this.proposalDescription = proposalDescription != null ? proposalDescription
				: defaultProposalDescription;
		return this;
	}

	public FieldOptions setProposalKeyStroke(final String proposalKeyStroke) {
		try {
			this.proposalKeyStroke = KeyStroke.getInstance(proposalKeyStroke);
		} catch (final ParseException e) {
			this.proposalKeyStroke = defaultProposalKeyStroke;
		}
		return this;
	}

	public FieldOptions setProposalProvider(
			final IContentProposalProvider proposalProvider) {
		this.proposalProvider = proposalProvider;
		return this;
	}

	public FieldOptions setTargetToModel(
			final UpdateValueStrategy targetToModelStrategy) {
		this.targetToModel = targetToModelStrategy;
		return this;
	}

	public void setValidator(final IValidator validator) {
		this.validator = validator;
	}

}

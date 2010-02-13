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
package eclipseutils.jface.databinding;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;



/**
 * Describes the various options to use when creating a field with
 * {@link Builder#field(String, FieldOptions)}.
 * 
 * <p>
 * It allows you to specify update strategies, a validator and content assist
 * options.
 * 
 * <h2>Example</h2>
 * <p>
 * 
 * <pre>
 * new StandardBuilder(parent, bean, UpdateValueStrategy.POLICY_CONVERT)
 *   .field("name", new FieldOptions(NotEmptyValidator.getInstance(true))
 *   .setContentProposalProvider(new NamesListProvider));
 * </pre>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class FieldOptions {
	private static final int defaultProposalAcceptanceStyle = ContentProposalAdapter.PROPOSAL_INSERT;
	private static final String defaultProposalDescription = "Use {0} to open the content assist";
	/** The default content assist proposal display keystroke is M1 + SPACE */
	private static final KeyStroke defaultProposalKeyStroke = KeyStroke
			.getInstance(SWT.MOD1, 32);

	private char[] autoActivationnCharacters;
	private int proposalAcceptanceStyle = defaultProposalAcceptanceStyle;
	private String proposalDescription = defaultProposalDescription;
	private KeyStroke proposalKeyStroke = defaultProposalKeyStroke;
	private IContentProposalProvider proposalProvider;
	private UpdateValueStrategy targetToModel;

	private IValidator validator;
	private ILabelProvider proposalLabelProvider;

	/**
	 * Creates a <code>FieldOptions</code> object with the given activation
	 * chars.
	 * 
	 * @param autoActivationnCharacters
	 * @see #setAutoActivationCharacters(char[])
	 * @see ContentProposalAdapter#setAutoActivationCharacters(char[])
	 */
	public FieldOptions(final char[] autoActivationnCharacters) {
		setAutoActivationCharacters(autoActivationnCharacters);
	}

	/**
	 * Creates a <code>FieldOptions</code> object with the given proposal
	 * provider.
	 * 
	 * @param proposalProvider
	 * @see #setProposalProvider(IContentProposalProvider)
	 */
	public FieldOptions(final IContentProposalProvider proposalProvider) {
		setProposalProvider(proposalProvider);
	}

	/**
	 * Creates a <code>FieldOptions</code> object with the given validator.
	 * 
	 * @param validator
	 * @see #setValidator(IValidator)
	 */
	public FieldOptions(final IValidator validator) {
		setValidator(validator);
	}

	/**
	 * Creates a <code>FieldOptions</code> object with the given proposal
	 * keystrokes.
	 * 
	 * <p>
	 * Please note: If the key stroke cannot be created, the default one (
	 * {@link #defaultProposalKeyStroke}) will be used instead.
	 * 
	 * @param proposalKeyStroke
	 * @see #setProposalKeyStroke(String)
	 */
	public FieldOptions(final String proposalKeyStroke) {
		setProposalKeyStroke(proposalKeyStroke);
	}

	/**
	 * Creates a <code>FieldOptions</code> object with the given
	 * <code>UpdateValueStrategy</code> for the targetToModel handling.
	 * 
	 * @param targetToModel
	 * @see #setTargetToModel(UpdateValueStrategy)
	 */
	public FieldOptions(final UpdateValueStrategy targetToModel) {
		setTargetToModel(targetToModel);
	}

	/**
	 * @return the set activation characters or <code>null</code> if none are
	 *         set.
	 */
	public char[] getAutoActivationnCharacters() {
		return autoActivationnCharacters;
	}

	/**
	 * @return the proposal acceptance style.
	 */
	public int getProposalAcceptanceStyle() {
		return proposalAcceptanceStyle;
	}

	/**
	 * @return the proposal description or <code>null</code> if none.
	 */
	public String getProposalDescription() {
		return proposalDescription;
	}

	/**
	 * @return the proposal keystroke or <code>null</code> if none.
	 */
	public KeyStroke getProposalKeyStroke() {
		return proposalKeyStroke;
	}

	/**
	 * @return the proposal provider or <code>null</code> if none.
	 */
	public IContentProposalProvider getProposalProvider() {
		return proposalProvider;
	}

	/**
	 * @return the Target->Model Strategy or <code>null</code> if none.
	 */
	public UpdateValueStrategy getTargetToModel() {
		return targetToModel;
	}

	/**
	 * @return the validator or <code>null</code> if none.
	 */
	public IValidator getValidator() {
		return validator;
	}

	/**
	 * @param autoActivationnCharacters
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setAutoActivationCharacters(
			final char... autoActivationnCharacters) {
		this.autoActivationnCharacters = autoActivationnCharacters;
		return this;
	}

	/**
	 * @param proposalAcceptanceStyle
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setProposalAcceptanceStyle(
			final int proposalAcceptanceStyle) {
		this.proposalAcceptanceStyle = proposalAcceptanceStyle;
		return this;
	}

	/**
	 * @param proposalDescription
	 *            If <code>null</code> then the default (
	 *            {@link #defaultProposalDescription} will be set.
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setProposalDescription(final String proposalDescription) {
		this.proposalDescription = proposalDescription != null ? proposalDescription
				: defaultProposalDescription;
		return this;
	}

	/**
	 * Sets the proposal keystroke.
	 * 
	 * <p>
	 * Please note: If the key stroke cannot be created, the default one (
	 * {@link #defaultProposalKeyStroke}) will be used instead.
	 * 
	 * @param proposalKeyStroke
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setProposalKeyStroke(final String proposalKeyStroke) {
		try {
			this.proposalKeyStroke = KeyStroke.getInstance(proposalKeyStroke);
		} catch (final ParseException e) {
			this.proposalKeyStroke = defaultProposalKeyStroke;
		}
		return this;
	}

	/**
	 * @param proposalProvider
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setProposalProvider(
			final IContentProposalProvider proposalProvider) {
		this.proposalProvider = proposalProvider;
		return this;
	}

	/**
	 * @param targetToModelStrategy
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setTargetToModel(
			final UpdateValueStrategy targetToModelStrategy) {
		this.targetToModel = targetToModelStrategy;
		return this;
	}

	/**
	 * @param validator
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setValidator(final IValidator validator) {
		this.validator = validator;
		return this;
	}

	/**
	 * @return the proposals label provider or <code>null</code> if none.
	 */
	public ILabelProvider getProposalLabelProvider() {
		return this.proposalLabelProvider;
	}

	/**
	 * Please not that the {@link IContentProposal} provided by your provider
	 * will be displayed using this label provider.
	 * 
	 * @param proposalLabelProvider
	 * @return <code>this</code> for chaining.
	 */
	public FieldOptions setProposalLabelProvider(
			final ILabelProvider proposalLabelProvider) {
		this.proposalLabelProvider = proposalLabelProvider;
		return this;
	}
}

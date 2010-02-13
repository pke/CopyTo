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

package eclipseutils.ui.copyto.internal.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;

import eclipseutils.ui.copyto.api.CopyService;
import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.internal.HttpCopyToHandler;
import eclipseutils.ui.copyto.internal.LogHelper;
import eclipseutils.ui.copyto.internal.api.Target;
import eclipseutils.ui.copyto.internal.api.TargetService;

/**
 * Default implementation for the CopyService.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class CopyServiceImpl implements CopyService {
	private final AtomicReference<TargetService> targetServiceRef = new AtomicReference<TargetService>();

	protected void bind(final TargetService service) {
		targetServiceRef.set(service);
	}

	protected void unbind(final TargetService service) {
		targetServiceRef.compareAndSet(service, null);
	}

	public Results copy(final String targetId, final IProgressMonitor monitor,
			final Copyable... copyables) {
		final TargetService targetService = targetServiceRef.get();
		if (targetService != null) {
			final Target target = targetService.find(targetId);
			if (null == target) {
				LogHelper.error(null, "Target \"%s\" not found", targetId);
				return null;
			}

			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					copyables.length);

			final ResultsImpl results = new ResultsImpl(target);
			final Map<String, String> params = new HashMap<String, String>();
			String urlParams = target.getUrl();
			final int indexOf = urlParams.indexOf('?');
			if (indexOf == -1) {
				return results;
			}
			final String url = urlParams.substring(0, indexOf);
			urlParams = urlParams.substring(indexOf + 1);
			final String[] pairs = urlParams.split("&"); //$NON-NLS-1$
			for (int i = 0; i < pairs.length; ++i) {
				final String[] keyValue = pairs[i].split("="); //$NON-NLS-1$
				params.put(keyValue[0], keyValue.length == 1 ? "" : keyValue[1]);
			}

			final HttpCopyToHandler handler = new HttpCopyToHandler(target
					.getId(), url);

			for (final Copyable copyable : copyables) {
				final Map<String, String> copyParams = resolveParams(copyable,
						params);
				results.add(handler.copy(copyable, target, copyParams, monitor));
			}
			targetService.setLastSelected(targetId);
			return results;
		} else {
			LogHelper.error(null, "No TargetService available");
			return null;
		}
	}

	private static Map<String, String> resolveParams(final Copyable copyable,
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

		final Map<String, String> result = new HashMap<String, String>(params
				.size());
		for (final Entry<String, String> entry : params.entrySet()) {
			try {
				result.put(entry.getKey(), variableManager
						.performStringSubstitution(entry.getValue(), false));
			} catch (final CoreException e) {
			}
		}
		variableManager.removeVariables(vars);
		return result;
	}
}

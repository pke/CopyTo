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

package eclipseutils.ui.copyto.internal.services;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Display;

import osgiutils.services.SimpleServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.ui.copyto.api.CopyService;
import eclipseutils.ui.copyto.api.Copyable;
import eclipseutils.ui.copyto.api.ResultHandler;
import eclipseutils.ui.copyto.api.Results;
import eclipseutils.ui.copyto.api.UIResultHandler;
import eclipseutils.ui.copyto.internal.LogHelper;
import eclipseutils.ui.copyto.internal.api.TargetService;
import eclipseutils.ui.copyto.internal.models.ResultImpl;
import eclipseutils.ui.copyto.internal.models.ResultsImpl;
import eclipseutils.ui.copyto.internal.models.Target;

/**
 * Default implementation for the CopyService.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class CopyServiceImpl implements CopyService {
	private final AtomicReference<TargetService> targetServiceRef = new AtomicReference<TargetService>();

	private final HttpCopyToHandler handler = new HttpCopyToHandler();

	protected void bind(final TargetService service) {
		targetServiceRef.set(service);
	}

	protected void unbind(final TargetService service) {
		targetServiceRef.compareAndSet(service, null);
	}

	public Results copy(final String targetId, final IProgressMonitor monitor,
			final IShellProvider shellProvider, final Copyable... copyables) {
		final TargetService targetService = targetServiceRef.get();
		if (targetService != null) {
			final Target target = targetService.find(targetId);
			if (null == target) {
				LogHelper.error(null, "Target \"%s\" not found", targetId); //$NON-NLS-1$
				return null;
			}

			final SubMonitor subMonitor = SubMonitor.convert(monitor,
					copyables.length);

			final ResultsImpl results = new ResultsImpl(target);
			/*final Map<String, String> params = new HashMap<String, String>();
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
				params.put(keyValue[0], keyValue.length == 1 ? "" : keyValue[1]); //$NON-NLS-1$
			}*/

			for (final Copyable copyable : copyables) {
				try {
					results.add(handler.copy(copyable, target, subMonitor));
				} catch (final Exception e) {
					results.add(new ResultImpl(copyable, target, e));
				}
			}
			targetService.setLastSelected(targetId);
			notifyListeners(results, shellProvider);
			return results;
		} else {
			LogHelper.error(null, "No TargetService available"); //$NON-NLS-1$
			return null;
		}
	}

	private void notifyListeners(final Results results,
			final IShellProvider shellProvider) {
		Trackers.runAll(ResultHandler.class,
				new SimpleServiceRunnable<ResultHandler>() {
					@Override
					protected void doRun(final ResultHandler service) {
						try {
							service.handleResults(results);
						} catch (final Throwable t) {
							LogHelper.error(t, "Calling result handler"); //$NON-NLS-1$
						}
					}
				});
		Trackers.runAll(UIResultHandler.class,
				new SimpleServiceRunnable<UIResultHandler>() {
					@Override
					protected void doRun(final UIResultHandler service) {
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								try {
									service.handleResults(results,
											shellProvider);
								} catch (final Throwable t) {
									LogHelper
											.error(t, "Calling result handler"); //$NON-NLS-1$
								}
							}
						});
					}
				});
	}
}

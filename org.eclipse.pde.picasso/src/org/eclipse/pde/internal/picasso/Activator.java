/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The Activator controls the plug-in life cycle.
 */
public class Activator extends AbstractUIPlugin {
	private Job createJob(Options options) {
		boolean toolTip = options.getToolTip();
		int extraCompositeMargin = options.getExtraCompositeMargin();
		Job job = new ListenerJob(Messages.ListenerJob_name, extraCompositeMargin, toolTip);
		return job;
	}

	private Properties loadOptionProperties() throws IOException {
		Properties properties = new Properties();
		Bundle bundle = getBundle();
		URL entry = bundle.getEntry(".options"); //$NON-NLS-1$
		InputStream stream = null;

		try {
			stream = entry.openStream();
			properties.load(stream);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}

		return properties;
	}

	private void scheduleJob() throws IOException {
		Properties properties = loadOptionProperties();
		Options options = new Options(properties);
		boolean paint = options.getPaint();
		if (paint == false) return;
		Job job = createJob(options);
		job.schedule();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		scheduleJob();
	}
}
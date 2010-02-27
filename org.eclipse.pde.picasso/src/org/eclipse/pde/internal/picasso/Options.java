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

import java.util.Properties;

import org.eclipse.core.runtime.Platform;

public class Options extends Object {
	public static final String PLUGIN_ID = "org.eclipse.pde.picasso"; //$NON-NLS-1$
	public static final String OPTION_ID_PAINT = "paint"; //$NON-NLS-1$
	public static final String OPTION_ID_PAINT_EXTRA_COMPOSITE_MARGIN = "paint/extraCompositeMargin"; //$NON-NLS-1$
	public static final String OPTION_ID_PAINT_TOOL_TIP = "paint/toolTip"; //$NON-NLS-1$

	private Properties properties;

	public Options(Properties properties) {
		super();
		this.properties = properties;
	}

	private String createOptionKey(String option) {
		return Options.PLUGIN_ID + '/' + option;
	}

	private boolean getBooleanOption(String option, boolean defaultValue) {
		String value = getOption(option);
		boolean result = value != null ? Boolean.valueOf(value).booleanValue() : defaultValue;
		return result;
	}

	private boolean getBooleanOptionDefault(String option, boolean defaultValue) {
		String key = createOptionKey(option);
		String value = System.getProperty(key);
		boolean result;

		if (value != null) {
			result = Boolean.valueOf(value).booleanValue();
		} else {
			String propertyValue = properties.getProperty(option);
			result = propertyValue != null ? Boolean.valueOf(propertyValue).booleanValue() : defaultValue;
		}

		return result;
	}

	public int getExtraCompositeMargin() {
		int defaultValue = getIntegerOptionDefault(Options.OPTION_ID_PAINT_EXTRA_COMPOSITE_MARGIN, 0);
		int value = getIntegerOption(Options.OPTION_ID_PAINT_EXTRA_COMPOSITE_MARGIN, defaultValue);
		return value;
	}

	private int getIntegerOption(String option, int defaultValue) {
		String value = getOption(option);
		int result = value != null ? Integer.parseInt(value) : defaultValue;
		return result;
	}

	private int getIntegerOptionDefault(String option, int defaultValue) {
		String key = createOptionKey(option);
		String value = System.getProperty(key);
		int result;

		if (value != null) {
			result = Integer.parseInt(value);
		} else {
			String propertyValue = properties.getProperty(option);
			result = propertyValue != null ? Integer.parseInt(propertyValue) : defaultValue;
		}
		return result;
	}

	private String getOption(String option) {
		String key = createOptionKey(option);
		return Platform.getDebugOption(key);
	}

	public boolean getPaint() {
		boolean defaultValue = getBooleanOptionDefault(Options.OPTION_ID_PAINT, false);
		boolean value = getBooleanOption(Options.OPTION_ID_PAINT, defaultValue);
		return value;
	}

	public boolean getToolTip() {
		boolean defaultValue = getBooleanOptionDefault(Options.OPTION_ID_PAINT_TOOL_TIP, false);
		boolean value = getBooleanOption(Options.OPTION_ID_PAINT_TOOL_TIP, defaultValue);
		return value;
	}
}

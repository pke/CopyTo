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
package copyto.core.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import copyto.core.TargetSelectionParam;

/**
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class ChoiceTargetParamModel extends StringTargetParamModel implements
		TargetSelectionParam {

	private LinkedHashMap<String, String> items;
	private boolean knownValue = true;

	public ChoiceTargetParamModel(String name, Map<String, String> items) {
		super(name, "");
		this.items = new LinkedHashMap<String, String>(items);
	}

	public ChoiceTargetParamModel(Preferences preferences) {
		super(preferences);
	}

	@Override
	protected String getId() {
		return "choice";
	}

	@Override
	public void doSave(Preferences preferences) {
		Preferences itemsNode = preferences.node("items");
		for (Entry<String, String> entry : items.entrySet()) {
			itemsNode.node(entry.getKey()).put("value", entry.getValue());
		}
		super.doSave(preferences);
	}

	public Map<String, String> getChoices() {
		return items;
	}

	public String[] getValues() {
		return items.values().toArray(new String[items.size()]);
	}

	@Override
	protected void doLoad(Preferences preferences) {
		// First create the items
		items = new LinkedHashMap<String, String>();
		try {
			if (preferences.nodeExists("items")) {
				Preferences itemNode = preferences.node("items");
				for (String name : itemNode.childrenNames()) {
					items.put(name, itemNode.node(name).get("value",
							"load error"));
				}
			}
		} catch (BackingStoreException e) {
		}
		// Cause this will call setValue and the items need to ready.
		super.doLoad(preferences);
	}

	@Override
	public String getLabelValue() {
		String inList = items.get(getValue());
		if (inList != null) {
			return inList;
		}
		return super.getLabelValue();
	}

	@Override
	public void setValue(String value) {
		knownValue = getChoices().containsKey(value);
		if (!knownValue) {
			for (Entry<String, String> entry : getChoices().entrySet()) {
				if (entry.getValue().equals(value)) {
					knownValue = true;
					value = entry.getKey();
					break;
				}
			}
		}
		super.setValue(value);
	}

	public boolean isKnownValue() {
		return knownValue;
	}

}

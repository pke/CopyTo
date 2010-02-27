package copyto.core.models;

import java.util.ArrayList;
import java.util.Collection;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import copyto.core.TargetParam;

public class TargetParamsModel extends AbstractModel {

	private Collection<TargetParam<?>> params = new ArrayList<TargetParam<?>>();

	public Collection<TargetParam<?>> getParams() {
		return params;
	}

	public void setParams(Collection<TargetParam<?>> params) {
		firePropertyChange("params", this.params, this.params = params);
	}

	public void save(Preferences preferences) {
		Preferences paramsNode = preferences.node("params");
		for (TargetParam<?> param : params) {
			Preferences paramName = paramsNode.node(param.getName());
			param.save(paramName);
		}
	}

	public void load(Preferences preferences) {
		try {
			if (preferences.nodeExists("params")) {
				Preferences node = preferences.node("params");
				params.clear();
				for (String name : node.childrenNames()) {
					Preferences paramNode = node.node(name);
					if (paramNode.nodeExists("boolean")) {
						params.add(new BooleanTargetParamModel(paramNode));
					} else if (paramNode.nodeExists("string")) {
						params.add(new StringTargetParamModel(paramNode));
					} else if (paramNode.nodeExists("choice")) {
						params.add(new ChoiceTargetParamModel(paramNode));
					}
				}
			}
		} catch (BackingStoreException e) {
		}
	}
}

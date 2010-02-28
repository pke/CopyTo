package copyto.target.http.core.internal;

import copyto.core.ExtensionTargetFactory;
import copyto.core.Target;

public class HttpTargetFactory extends ExtensionTargetFactory {

	public Target createTarget() {
		return new HttpTargetModel(this);
	}	
}

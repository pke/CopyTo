package copyto.paste.chat.miranda.internal;

import org.eclipse.core.expressions.PropertyTester;

import copyto.paste.chat.miranda.MirandaIRC;

public class MirandaPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if ("available".equals(property)) {
			return MirandaIRC.find() != null;
		}
		return false;
	}

}

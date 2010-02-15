package eclipseutils.ui.copyto.internal.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.ui.copyto.internal.api.Target;
import eclipseutils.ui.copyto.internal.api.TargetService;

/**
 * Converts Targets into their String IDs and vice versa.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class TargetParameterConverter extends AbstractParameterValueConverter {

	@Override
	public Object convertToObject(final String parameterValue)
			throws ParameterValueConversionException {
		return Trackers.run(TargetService.class,
				new ServiceRunnable<TargetService, Object>() {
					public Object run(final TargetService service) {
						return service.find(parameterValue);
					}
				});
	}

	@Override
	public String convertToString(final Object parameterValue)
			throws ParameterValueConversionException {
		if (parameterValue instanceof Target) {
			return ((Target) parameterValue).getId();
		}
		throw new ParameterValueConversionException(
				"Expected Target for id parameter"); //$NON-NLS-1$
	}

}

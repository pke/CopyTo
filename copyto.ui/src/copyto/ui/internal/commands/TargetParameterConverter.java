package copyto.ui.internal.commands;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;

import osgiutils.services.ServiceRunnable;
import osgiutils.services.Trackers;
import copyto.core.TargetDescriptor;
import copyto.core.TargetService;

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
				new ServiceRunnable<TargetService, TargetDescriptor>() {
					public TargetDescriptor run(final TargetService service) {
						return service.find(parameterValue);
					}
				});
	}

	@Override
	public String convertToString(final Object parameterValue)
			throws ParameterValueConversionException {
		if (parameterValue instanceof TargetDescriptor) {
			return ((TargetDescriptor) parameterValue).getId();
		}
		throw new ParameterValueConversionException(
				"Expected Target for id parameter"); //$NON-NLS-1$
	}

}

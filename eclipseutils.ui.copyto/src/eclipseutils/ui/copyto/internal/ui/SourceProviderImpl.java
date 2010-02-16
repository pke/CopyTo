package eclipseutils.ui.copyto.internal.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import osgiutils.services.DefaultCollectionServiceRunnable;
import osgiutils.services.Trackers;
import eclipseutils.ui.copyto.internal.api.TargetService;
import eclipseutils.ui.copyto.internal.api.TargetServiceListener;
import eclipseutils.ui.copyto.internal.models.Target;

/**
 * Source provider for copyto.
 * 
 * <p>
 * Available variables: <code>
 * coptyo.targets
 * </code>
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public class SourceProviderImpl extends AbstractSourceProvider implements
		TargetServiceListener {

	private static final String COPYTO_TARGETS = "copyto.targets"; //$NON-NLS-1$
	private final ServiceRegistration serviceRegistration;

	/**
	 * Creates the service provider and registers it as a TargetServiceListener.
	 */
	public SourceProviderImpl() {
		serviceRegistration = FrameworkUtil.getBundle(getClass())
				.getBundleContext().registerService(
						TargetServiceListener.class.getName(), this, null);
	}

	public void dispose() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

	@SuppressWarnings("rawtypes")
	public Map getCurrentState() {
		final Map<String, Object> state = new HashMap<String, Object>();
		state.put(COPYTO_TARGETS, getTargets());
		return state;
	}

	public String[] getProvidedSourceNames() {
		return new String[] { COPYTO_TARGETS };
	}

	public void targetsChanged(final Collection<Target> changedTargets) {
		fireSourceChanged(ISources.WORKBENCH, COPYTO_TARGETS, getTargets());
	}

	private Collection<Target> getTargets() {
		return Trackers.run(TargetService.class,
				new DefaultCollectionServiceRunnable<TargetService, Target>() {
					public Collection<Target> run(final TargetService service) {
						return service.findAll();
					}
				});
	}

}

package eclipseutils.ui.copyto.internal;

import java.util.Collection;
import java.util.List;

/**
 * Provides management of CopyTo Targets.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface TargetService {

	Target find(String id);

	Target findFirst();

	List<Target> findAll();

	void save(Collection<Target> targets);

	Target getLastSelected();

	void setLastSelected(String id);

	/**
	 * @return number of available Targets.
	 */
	int count();
}

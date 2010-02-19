package copyto.core;

import java.util.Collection;
import java.util.List;


/**
 * Provides management of CopyTo Targets.
 * 
 * @author <a href="mailto:phil.kursawe@gmail.com">Philipp Kursawe</a>
 * 
 */
public interface TargetService {

	/**
	 * Finds a target by its unique id.
	 * 
	 * @param id
	 * @return the found target or <code>null</code> if no target with the given
	 *         <i>id</i> exist.
	 */
	Target find(String id);

	/**
	 * @return the first found target or <code>null</code> if no targets exist.
	 */
	Target findFirst();

	/**
	 * @return a list of all targets in abitrary order.
	 */
	List<Target> findAll();

	/**
	 * @param targets
	 */
	void save(Collection<Target> targets);

	/**
	 * @return target
	 */
	Target getLastSelected();

	/**
	 * @param id
	 */
	void setLastSelected(String id);

	/**
	 * @return number of available Targets.
	 */
	int count();
}

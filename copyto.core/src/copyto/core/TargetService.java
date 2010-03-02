package copyto.core;

import java.io.InputStream;
import java.io.OutputStream;
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
	TargetDescriptor find(String id);

	/**
	 * @return the first found target or <code>null</code> if no targets exist.
	 */
	TargetDescriptor findFirst();

	/**
	 * @return a list of all targets in abitrary order.
	 */
	List<TargetDescriptor> findAll();

	/**
	 * @param targets
	 */
	void save(Collection<Target> targets);

	/**
	 * @return target
	 */
	TargetDescriptor getLastSelected();

	/**
	 * @param id
	 */
	void setLastSelected(String id);

	/**
	 * @return number of available Targets.
	 */
	int count();

	OutputStream exportToStream(String id);

	void importFromStream(InputStream is);
}

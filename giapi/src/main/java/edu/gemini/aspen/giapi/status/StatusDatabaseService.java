package edu.gemini.aspen.giapi.status;

import java.util.Collection;
import java.util.Set;

/**
 * The public interface of a database service for Status Items
 */
public interface StatusDatabaseService {


    /**
     * Returns the status item associated to the given name or
     * <code>null</code> if there is no status item with that
     * name
     *
     * @param name name of the status item to look for
     * @return the Status Item associated to the name if it is
     *         available, or <code>null</code> if there is no status
     *         item in the database associated to the given name
     */
    <T> StatusItem<T> getStatusItem(String name);

    /**
     * Returns a Set containing all the registered status names in the database
     *
     * @return the Set of status names
     */
    Set<String> getStatusNames();

    /**
     * Returns an Iterable containing all the StatusItems in the database
     *
     * @return the Iterable of StatusItems
     */
    Collection<StatusItem> getAll();
}

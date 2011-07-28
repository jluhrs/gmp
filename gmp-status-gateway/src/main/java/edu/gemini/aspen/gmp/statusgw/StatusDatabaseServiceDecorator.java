package edu.gemini.aspen.gmp.statusgw;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import edu.gemini.aspen.giapi.status.StatusItem;

import java.util.Collection;
import java.util.Set;

/**
 * This Status Database decorator simply allows having a long-lived
 * StatusDatabaseService in use by the system. The actual operation of this
 * service is delegated to the registered StatusDatabaseService that
 * can come and go.
 */
public class StatusDatabaseServiceDecorator implements StatusDatabaseService {

    private StatusDatabaseService _service = null;

    public <T> StatusItem<T> getStatusItem(String name) {
        if (_service != null) {
            return _service.getStatusItem(name);
        }
        return null;
    }

    /**
     * Gets a Set with all the status names.
     *
     * @return status names
     */
    public Set<String> getStatusNames() {
        if (_service != null) {
            return _service.getStatusNames();
        }
        return null;
    }

    @Override
    public Collection<StatusItem> getAll() {
        if (_service != null) {
            return _service.getAll();
        }
        return null;
    }

    /**
     * Registers the real DatabaseService to be use by this decorator. Any
     * existing database service will be removed.
     *
     * @param service the Status Database Service to be used
     */
    public void setDatabaseService(StatusDatabaseService service) {
        _service = service;
    }

    /**
     * Remove the existing database service from the system.
     */
    public void removeDatabaseService() {
        _service = null;
    }

}

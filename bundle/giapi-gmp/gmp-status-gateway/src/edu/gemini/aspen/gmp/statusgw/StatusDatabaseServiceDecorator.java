package edu.gemini.aspen.gmp.statusgw;

import edu.gemini.aspen.gmp.status.StatusDatabaseService;
import edu.gemini.aspen.gmp.status.StatusItem;

/**
 * This Status Database decorator simply allows having a long-lived
 * StatusDatabaseService in use by the system. The actual operation of this
 * service is delegated to the registered StatusDatabaseService that
 * can come and go.
 */
public class StatusDatabaseServiceDecorator implements StatusDatabaseService {

    private StatusDatabaseService _service = null;

    public StatusItem getStatusItem(String name) {
        if (_service != null) {
            return _service.getStatusItem(name);
        }
        return null;  
    }

    /**
     * Registers the real DatabaseService to be use by this decorator. Any
     * existing database service will be removed.
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

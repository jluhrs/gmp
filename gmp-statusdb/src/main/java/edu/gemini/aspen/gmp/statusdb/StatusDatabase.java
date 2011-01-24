package edu.gemini.aspen.gmp.statusdb;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.StatusDatabaseService;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Status Database contains the most up to date information related to
 * the status items. All the status items received by the GMP are recorded here
 */
public class StatusDatabase implements StatusHandler, StatusDatabaseService {

    //Store the most recent information associated to all the status items
    final private ConcurrentHashMap<String, StatusItem> _db
            = new ConcurrentHashMap<String, StatusItem>();


    public StatusDatabase() {
    }

    public String getName() {
        return "Status Database";  
    }


    public StatusItem getStatusItem(String name) {
        return _db.get(name);
    }

    public Set<String> getStatusNames(){
        return Collections.unmodifiableSet(_db.keySet());
    }
    
    public void update(StatusItem item) {
        //store this new value in the database
        _db.put(item.getName(), item);
    }

    @Override
    public String toString() {
        return getName();
    }
}

package edu.gemini.aspen.gmp.statusservice.core;

import edu.gemini.aspen.gmp.status.StatusItem;

/**
 * This is an internal interface for services that will get a
 * notification when a status item is received through a messaging
 * system, like JMS.
 * <p/>
 * This interface is similar to the <code>StatusHandler</code>, but
 * the difference is that this is used only in the context of the status
 * service that receive status items as messages from instruments, whereas
 * the Status Handler receives updates internally in the GMP.  
 */
public interface StatusUpdater {

    void update(StatusItem item);

}

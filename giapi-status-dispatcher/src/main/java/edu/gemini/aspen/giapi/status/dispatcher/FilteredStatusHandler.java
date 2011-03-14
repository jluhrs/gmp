package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusHandler;

/**
 * Interface FilteredStatusHandler
 *
 * @author Nicolas A. Barriga
 *         Date: 2/24/11
 */
public interface FilteredStatusHandler extends StatusHandler {
    /**
     * Returns the filter to determine which status items to subscribe to.
     *
     * @return a StatusItemFilter representing the filter
     */
    StatusItemFilter getFilter();
}

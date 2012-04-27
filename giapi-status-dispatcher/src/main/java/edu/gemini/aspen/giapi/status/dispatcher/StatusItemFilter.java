package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;

import java.util.logging.Logger;

/**
 * Interface StatusItemFilter
 *
 * @author Nicolas A. Barriga
 *         Date: 4/26/12
 */
public interface StatusItemFilter {
    final static Logger LOG = Logger.getLogger(StatusItemFilter.class.getName());

    boolean match(StatusItem item);
}

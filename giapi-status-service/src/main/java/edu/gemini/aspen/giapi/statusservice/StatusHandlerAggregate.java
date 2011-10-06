package edu.gemini.aspen.giapi.statusservice;

import edu.gemini.aspen.giapi.status.StatusHandler;

/**
 * Interface required to expose StatusHandlerAggregateImpl as an
 * OSGi service
 */
public interface StatusHandlerAggregate extends StatusHandler {
    void bindStatusHandler(StatusHandler handler);

    void unbindStatusHandler(StatusHandler handler);
}

package edu.gemini.aspen.gmp.broker.api;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 *
 */
public interface GMPService {

//    public void addStatusHandler(StatusHandler handler);
//
//    public void removeStatusHandler(StatusHandler handler);

    void start();

    void shutdown();


    HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity);

    HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config);

}

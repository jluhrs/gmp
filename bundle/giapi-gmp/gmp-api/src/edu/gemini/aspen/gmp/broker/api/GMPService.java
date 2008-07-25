package edu.gemini.aspen.gmp.broker.api;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 * Defines the operations supported by the GMP Service
 */
public interface GMPService {

    /**
     * Start the Gemini Master Process Service
     */
    void start();

    /**
     * Shutdown the Gemini Master Process Service
     */
    void shutdown();


    /**
     * Send a SequenceCommand with the specified activity to the registered
     * clients.
     *
     * Synchronously wait for the recipient to notify the command was
     * received and returns a HandlerResponse back to the caller.
     *
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     * specified sequence command, like PRESET or START
     * @return a HandlerResponse, used to decide if the command was accepted
     * by the client.
     */
    HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity);


    /**
     * Send a SequenceCommand with the specified activity and configuration
     * to the registered clients.
     *
     * Synchronously wait for the recipient to notify the command was
     * received and returns a HandlerResponse back to the caller.
     *
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     * specified sequence command, like PRESET or START
     * @param config the configuration that will be send along with the
     * sequence command
     * @return a HandlerResponse, used to decide if the command was accepted
     * by the client.
     */
    HandlerResponse sendSequenceCommand(SequenceCommand command, Activity activity, Configuration config);

}

package edu.gemini.aspen.giapi.commands;

/**
 * Specify how to send sequence commands down to an instrument
 */
public interface CommandSender {

    /**
     * Send a SequenceCommand with the specified activity to the registered
     * clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param listener This listener will be invoked to provide completion
     *                 information about this sequence command.
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    HandlerResponse sendSequenceCommand(SequenceCommand command,
                                        Activity activity,
                                        CompletionListener listener);


    /**
     * Send a SequenceCommand with the specified activity and configuration to
     * the registered clients.
     * <p/>
     * Synchronously wait for the recipient to notify the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer for a defined period of time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Sequence command to send, like INIT or REBOOT
     * @param activity The associated activities to be executed for the
     *                 specified sequence command, like PRESET or START
     * @param config   the configuration that will be send along with the
     *                 sequence command
     * @param listener This listener will be invoked to provide completion
     *                 information about this sequence command.
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    HandlerResponse sendSequenceCommand(SequenceCommand command,
                                        Activity activity,
                                        Configuration config,
                                        CompletionListener listener);


}

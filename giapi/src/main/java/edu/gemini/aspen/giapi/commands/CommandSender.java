package edu.gemini.aspen.giapi.commands;

/**
 * Specify how to send sequence commands down to an instrument
 */
public interface CommandSender {

    /**
     * Sends a Command to a registered handler.
     * <p/>
     * Synchronously wait for the recipient to notify that the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no answer after a predefined time, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Command to execute
     * @param listener This listener will be invoked to provide completion
     *                 information about this command.
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    HandlerResponse sendCommand(Command command, CompletionListener listener);

    /**
     * Sends a Command to the registered clients and waits a maximum timeout.
     * <p/>
     * Synchronously wait for the recipient to notify that the command was received
     * and returns a HandlerResponse back to the caller.
     * <p/>
     * If there is no immediate answer, the call will return
     * a HandlerResponse containing an error message.
     *
     * @param command  The Command to execute
     * @param listener This listener will be invoked to provide completion
     *                 information about this command.
     * @param timeout  Maximum time to wait for an answer in milliseconds
     *
     * @return a HandlerResponse, used to decide if the command was accepted by
     *         the client.
     */
    HandlerResponse sendCommand(Command command, CompletionListener listener, long timeout);
}

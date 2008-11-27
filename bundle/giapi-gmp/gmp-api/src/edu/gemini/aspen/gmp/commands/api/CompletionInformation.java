package edu.gemini.aspen.gmp.commands.api;

/**
 *
 */
public interface CompletionInformation {

    HandlerResponse getHandlerResponse();

    SequenceCommand getSequenceCommand();

    Activity getActivity();

    Configuration getConfiguration();

}

package edu.gemini.aspen.gmp.commands;

/**
 *
 */
public interface CompletionInformation {

    HandlerResponse getHandlerResponse();

    SequenceCommand getSequenceCommand();

    Activity getActivity();

    Configuration getConfiguration();

}

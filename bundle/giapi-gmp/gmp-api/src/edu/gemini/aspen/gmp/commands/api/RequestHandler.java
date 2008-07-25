package edu.gemini.aspen.gmp.commands.api;

/**
 * Operation to be executed when receiving a request from the client to
 * the brokers
 */
public interface RequestHandler {

    void process(Request request);

}

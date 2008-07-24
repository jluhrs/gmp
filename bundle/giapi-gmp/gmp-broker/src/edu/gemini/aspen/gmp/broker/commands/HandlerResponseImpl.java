package edu.gemini.aspen.gmp.broker.commands;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;

/**
 * 
 */
public class HandlerResponseImpl implements HandlerResponse {

    private HandlerResponseImpl(Response response, String message) {
        _response = response;
        _message = message;
    }

    private HandlerResponseImpl(Response response) {
        this(response, null);
    }

    private HandlerResponseImpl(String message) {
        this(Response.ERROR, message);
    }

    private String _message;
    private Response _response;


    public Response getResponse() {
        return _response;
    }

    public String getMessage() {
        return _message;
    }

    public static HandlerResponse create(Response response) {
        return new HandlerResponseImpl(response);
    }

    public static HandlerResponse create(String errorMsg) {
        return new HandlerResponseImpl(errorMsg);
    }

    public static HandlerResponse create(Response response, String errorMsg) {
        return new HandlerResponseImpl(response, errorMsg);
    }



}

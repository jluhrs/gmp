package edu.gemini.aspen.gmp.util.commands;

import edu.gemini.aspen.gmp.commands.HandlerResponse;

/**
 * Implements the Handler Response. Provide factory methods
 * to instantiate new HandlerResponses
 */
public final class HandlerResponseImpl implements HandlerResponse {

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

    /**
	 * Static factory initializer. Takes as an argument
	 * the type of the response
     * @param response The response type the HandlerResponse to be
     * returned will have, like ACCEPTED
     * @return a HandlerResponse with the corresponding response type.
	 */
    public static HandlerResponse create(Response response) {
        return new HandlerResponseImpl(response);
    }

    /**
	 * Static factory initializer for error HandlerResponse.
	 * The response type is set to ERROR and the message is
	 * stored.
     * @param errorMsg The error message that will be associated to the
     * error HandlerResponse that will be returned.
     * @return an error HandlerResponse with the corresponding error message
     * set.
	 */
    public static HandlerResponse createError(String errorMsg) {
        return new HandlerResponseImpl(errorMsg);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (_response != null) {
            sb.append("[").append(_response.getTag());

            if (_response == Response.ERROR) {
                //we should have a message
                sb.append(" {");
                if (_message != null) {
                    sb.append(_message);
                }
                sb.append("}");
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HandlerResponseImpl that = (HandlerResponseImpl) o;

        if (_message != null ? !_message.equals(that._message) : that._message != null)
            return false;
        if (_response != that._response) return false;
        //nothing to be done
        return true;
    }

    @Override
    public int hashCode() {
        int result = _message != null ? _message.hashCode() : 0;
        result = 31 * result + _response.hashCode();
        return result;
    }
}

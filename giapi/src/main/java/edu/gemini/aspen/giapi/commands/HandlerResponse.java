package edu.gemini.aspen.giapi.commands;

import com.google.common.base.Preconditions;

/**
 * Handler response interface. Contains an enumerated response type
 * and a message when an error is produced.
 */
public final class HandlerResponse {

    /**
     * The Accepted Handler Response
     */
    public static HandlerResponse ACCEPTED = new HandlerResponse(Response.ACCEPTED);

    /**
     * The Started Handler Response
     */
    public static HandlerResponse STARTED = new HandlerResponse(Response.STARTED);

    /**
     * The Completed Handler Response
     */
    public static HandlerResponse COMPLETED = new HandlerResponse(Response.COMPLETED);

    /**
     * The No Answer Handler Response
     */
    public static HandlerResponse NOANSWER = new HandlerResponse(Response.NOANSWER);

    /**
     * Creates an Error Handler Response with the given error message
     *
     * @param errorMsg The error message that will be associated to the
     *                 error HandlerResponse that will be returned.
     * @return an error HandlerResponse with the corresponding error message
     *         set.
     */
    public static HandlerResponse createError(String errorMsg) {
        return new HandlerResponse(errorMsg != null?errorMsg:"");
    }

    public enum Response {
        /**
         * Action Accepted.
         */
        ACCEPTED("ACCEPTED"),

        /**
         * Actions started
         */
        STARTED("STARTED"),
        /**
         * Actions completed
         */
        COMPLETED("COMPLETED"),
        /**
         * Request ended with error.
         */
        ERROR("ERROR"),
        /**
         * Special error handler response. Created internally
         * by the system when for a given command, there is no
         * reply
         */
        NOANSWER("NOANSWER");

        Response(String tag) {
            _tag = tag;
        }

        private String _tag;

        public String getTag() {
            return _tag;
        }
    }

    private HandlerResponse(Response response, String message) {
        Preconditions.checkArgument(response != null, "Cannot create a HandlerResponse without a response type");
        Preconditions.checkArgument(message != null, "Cannot create a HandlerResponse without a message");

        _response = response;
        _message = message;
    }

    private HandlerResponse(Response response) {
        this(response, "");
    }

    private HandlerResponse(String message) {
        this(Response.ERROR, message);
    }

    private final String _message;
    private final Response _response;

    /**
     * Return the response type.
     *
     * @return The response type
     */
    public Response getResponse() {
        return _response;
    }

    public boolean hasErrorMessage() {
        return _response == Response.ERROR && !_message.isEmpty();
    }

    /**
     * Return the message associated to this handler
     * response. If the response type is not ERROR,
     * the return value is NULL.
     *
     * @return the message associated to an ERROR response type or NULL if
     *         the response is not ERROR.
     */
    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HandlerResponse that = (HandlerResponse) o;

        if (_message != null ? !_message.equals(that._message) : that._message != null) {
            return false;
        }
        if (_response != that._response) {
            return false;
        }
        //nothing to be done
        return true;
    }

    @Override
    public int hashCode() {
        int result = _message != null ? _message.hashCode() : 0;
        result = 31 * result + _response.hashCode();
        return result;
    }

    /**
     * Gets a HandlerResponse object for the given response
     *
     * @param response The response type the HandlerResponse to be
     *                 returned will have, like ACCEPTED
     * @return a HandlerResponse with the corresponding response type. If
     *         the response is "ERROR", an empty ERROR handler response will be returned
     */
    public static HandlerResponse get(HandlerResponse.Response response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }
        switch ( response) {
            case ACCEPTED:
                return HandlerResponse.ACCEPTED;
            case STARTED:
                return HandlerResponse.STARTED;
            case COMPLETED:
                return HandlerResponse.COMPLETED;
            case ERROR:
                return HandlerResponse.createError("");
            case NOANSWER:
                return HandlerResponse.NOANSWER;
            default:
                throw new IllegalArgumentException("Unknown response type " + response);
        }
    }

}

package edu.gemini.aspen.gmp.commands;

/**
 * Handler response interface. Contains an enumerated response type
 * and a message when an error is produced.
 */
public interface HandlerResponse {

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
    /**
	 * Return the response type.
     * @return The response type
	 */
    public Response getResponse();

    /**
	 * Return the message associated to this handler
	 * response. If the response type is not ERROR,
	 * the return value is NULL.
     * @return the message associated to an ERROR response type or NULL if
     * the response is not ERROR.  
	 */
    public String getMessage();

}

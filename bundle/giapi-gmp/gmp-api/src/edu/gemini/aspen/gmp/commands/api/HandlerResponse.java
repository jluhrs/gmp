package edu.gemini.aspen.gmp.commands.api;

/**
 *
 */
public interface HandlerResponse {

    public enum Response {
        ACCEPTED("ACCEPTED"),
        STARTED("STARTED"),
        COMPLETED("COMPLETED"),
        ERROR("ERROR");

        Response(String tag) {
            _tag = tag;
        }

        private String _tag;

        public String getTag() {
            return _tag;
        }
    }

    public Response getResponse();
    public String getMessage();

}

package edu.gemini.aspen.gmp.commands;

/**
 *  Completion Information class
 */
public class CompletionInformation {

    private final HandlerResponse _handlerResponse;

    private final SequenceCommand _command;

    private final Activity _activity;

    private final Configuration _configuration;

    public CompletionInformation(HandlerResponse handlerResponse,
                                     SequenceCommand command,
                                     Activity activity,
                                     Configuration configuration) {
        _handlerResponse = handlerResponse;
        _command = command;
        _activity = activity;
        _configuration = configuration;
    }

    /**
     * Returns the handler response associated to this completion information
     * @return the HandlerResponse
     */
    public HandlerResponse getHandlerResponse() {
        return _handlerResponse;
    }

    /**
     * Returns the Sequence Command associated to this completion information
     * @return the sequence command
     */
    public SequenceCommand getSequenceCommand() {
        return _command;
    }

    /**
     * Return the activity associated to this completion information
     * @return the Activity
     */
    public Activity getActivity() {
        return _activity;
    }

    /**
     * Return the configuration associated to this completion information
     * @return the command configuration
     */
    public Configuration getConfiguration() {
        return _configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompletionInformation that = (CompletionInformation) o;

        if (_activity != that._activity) return false;
        if (_command != that._command) return false;
        if (_configuration != null ? !_configuration.equals(that._configuration) : that._configuration != null)
            return false;
        if (_handlerResponse != null ? !_handlerResponse.equals(that._handlerResponse) : that._handlerResponse != null)
            return false;
        //everything is okay, they are equals
        return true;
    }

    @Override
    public int hashCode() {
        int result = _handlerResponse != null ? _handlerResponse.hashCode() : 0;
        result = 31 * result + (_command != null ? _command.hashCode() : 0);
        result = 31 * result + (_activity != null ? _activity.hashCode() : 0);
        result = 31 * result + (_configuration != null ? _configuration.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        if (_handlerResponse != null) {
            sb.append("[response=").append(_handlerResponse);
            sb.append("]");
        }

        if (_command != null) {
            sb.append("[command=").append(_command.name());
            sb.append("]");
        }

        if (_activity != null) {
            sb.append("[activity=").append(_activity.name());
            sb.append("]");
        }

        if (_configuration != null) {
            sb.append("[").append(_configuration);
            sb.append("]");
        }
        sb.append("]");

        return sb.toString();
    }

}

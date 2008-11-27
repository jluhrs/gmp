package edu.gemini.aspen.gmp.util.commands;

import edu.gemini.aspen.gmp.commands.api.HandlerResponse;
import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;

/**
 *
 */
public class CompletionInformationImpl {


    private HandlerResponse _handlerResponse;

    private SequenceCommand _command;

    private Activity _activity;

    private Configuration _configuration;

    public CompletionInformationImpl(HandlerResponse handlerResponse,
                                     SequenceCommand command,
                                     Activity activity,
                                     Configuration configuration) {
        _handlerResponse = handlerResponse;
        _command = command;
        _activity = activity;
        _configuration = configuration;
    }

    public HandlerResponse getHandlerResponse() {
        return _handlerResponse;
    }

    public SequenceCommand getCommand() {
        return _command;
    }

    public Activity getActivity() {
        return _activity;
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompletionInformationImpl that = (CompletionInformationImpl) o;

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
}

package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.CompletionListener;

/**
 * Actions are used to keep track of sequence command progress.
 */
public class Action implements Comparable<Action> {

    private static int ID = 0;
    private int _actionId;
    private SequenceCommand _sequenceCommand; //Sequence Command associated to it.
    private Activity _activity;
    private Configuration _configuration;
    private CompletionListener _listener;

    public Action(SequenceCommand sequenceCommand,
                    Activity activity,
                    Configuration configuration,
                    CompletionListener listener) {
        _actionId = ++ID;
        _sequenceCommand = sequenceCommand;
        _activity = activity;
        _configuration = configuration;
        _listener = listener;
    }

    public static int getCurrentId() {
        return ID;
    }

    public Activity getActivity() {
        return _activity;
    }

    public Configuration getConfiguration() {
        return _configuration;
    }

    public SequenceCommand getSequenceCommand() {
        return _sequenceCommand;
    }

    public int getId() {
        return _actionId;
    }

    public CompletionListener getCompletionListener() {
        return _listener;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (_actionId != action._actionId) return false;
        if (_activity != action._activity) return false;
        if (_configuration != null ?
                !_configuration.equals(action._configuration)
                : action._configuration != null)
            return false;
        if (_sequenceCommand != action._sequenceCommand) return false;
        if (_listener != action._listener) return false;
        //the objects are equals
        return true;
    }

    public int hashCode() {
        int result;
        result = _actionId;
        result = 31 * result +
                (_sequenceCommand != null ? _sequenceCommand.hashCode() : 0);
        result = 31 * result + (_activity != null ? _activity.hashCode() : 0);
        result = 31 * result +
                (_configuration != null ? _configuration.hashCode() : 0);
        result = 31 * result + (_listener != null ? _listener.hashCode() : 0);
        return result;
    }


    public int compareTo(Action other) {
        return _actionId - other.getId();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(_actionId).append(":").
                append(_sequenceCommand).append("/").
                append(_activity);
        if (_configuration != null) {
            sb.append(" {").append(_configuration).append("}");
        }
        if (_listener != null) {
            sb.append(" {").append(_listener).append("}");
        }
        sb.append("]");
        return sb.toString();
    }
}

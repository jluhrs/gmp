package edu.gemini.aspen.gmp.broker.commands;

import edu.gemini.aspen.gmp.commands.api.SequenceCommand;
import edu.gemini.aspen.gmp.commands.api.Activity;
import edu.gemini.aspen.gmp.commands.api.Configuration;
import edu.gemini.aspen.gmp.commands.api.CompletionListener;

/**
 * The Action ID is used to keep track of sequence command progress.
 */
public class ActionId implements Comparable<ActionId> {

    private static int ID = 0;
    private int _actionId;
    private SequenceCommand _sequenceCommand; //Sequence Command associated to it.
    private Activity _activity;
    private Configuration _configuration;
    private CompletionListener _listener;

    public ActionId(SequenceCommand sequenceCommand,
                    Activity activity,
                    Configuration configuration,
                    CompletionListener listener) {
        _actionId = ++ID;
        _sequenceCommand = sequenceCommand;
        _activity = activity;
        _configuration = configuration;
        _listener = listener;
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

    public int getActionId() {
        return _actionId;
    }

    public CompletionListener getCompletionListener() {
        return _listener;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionId actionId = (ActionId) o;

        if (_actionId != actionId._actionId) return false;
        if (_activity != actionId._activity) return false;
        if (_configuration != null ?
                !_configuration.equals(actionId._configuration)
                : actionId._configuration != null)
            return false;
        if (_sequenceCommand != actionId._sequenceCommand) return false;
        if (_listener != actionId._listener) return false;
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


    public int compareTo(ActionId other) {
        return _actionId - other.getActionId();
    }

}

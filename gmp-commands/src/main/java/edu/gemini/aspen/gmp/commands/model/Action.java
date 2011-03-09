package edu.gemini.aspen.gmp.commands.model;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionListener;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Actions are used to keep track of sequence command progress.
 */
public final class Action implements Comparable<Action> {

    private static AtomicInteger ID = new AtomicInteger();
    private final int _actionId;
//    private final SequenceCommand _sequenceCommand; //Sequence Command associated to it.
//    private final Activity _activity;
//    private final Configuration _configuration;
    private final CompletionListener _listener;

    private final Command _command;

    public Action(Command command, CompletionListener listener) {
        Preconditions.checkArgument(command != null, "Action's command cannot be null");
        Preconditions.checkArgument(listener != null, "Action's listener cannot be null");
        this._command = command;
        this._listener = listener;
        _actionId = ID.incrementAndGet();
    }

    Action(int actionId, Command command, CompletionListener listener) {
        Preconditions.checkArgument(command != null, "Action's command cannot be null");
        Preconditions.checkArgument(listener != null, "Action's listener cannot be null");
        this._command = command;
        this._listener = listener;
        _actionId = actionId;
    }

//    public Action(SequenceCommand sequenceCommand,
//                    Activity activity,
//                    Configuration configuration,
//                    CompletionListener listener) {
//        _actionId = ID.incrementAndGet();
//        _sequenceCommand = sequenceCommand;
//        _activity = activity;
//        _configuration = configuration;
//        _listener = listener;
//    }
//
//    Action(int actionID,
//           SequenceCommand sequenceCommand,
//                    Activity activity,
//                    Configuration configuration,
//                    CompletionListener listener) {
//        _actionId = actionID;
//        _sequenceCommand = sequenceCommand;
//        _activity = activity;
//        _configuration = configuration;
//        _listener = listener;
//    }
//
    public static int getCurrentId() {
        return ID.get();
    }

    public int getId() {
        return _actionId;
    }

    public Command getCommand() {
        return _command;
    }

    @Deprecated
    public CompletionListener getCompletionListener() {
        return _listener;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (_actionId != action._actionId) return false;
        if (!_command.equals(action._command)) return false;
        if (!_listener.equals(action._listener)) return false;
        //the objects are equals
        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = _actionId;
        result = 31 * result + _command.hashCode();
        result = 31 * result + _listener.hashCode();
        return result;
    }


    @Override
    public int compareTo(Action other) {
        return _actionId - other.getId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(_actionId).append(":").
                append(_command);
        sb.append(" {").append(_listener).append("}");
        sb.append("]");
        return sb.toString();
    }
}

package edu.gemini.aspen.giapi.commands;

import com.google.common.base.Preconditions;

/**
 * Completion Information class
 */
public class CompletionInformation {

    private final HandlerResponse _handlerResponse;
    private final Command _command;

    public CompletionInformation(HandlerResponse handlerResponse,
                                 Command command) {
        Preconditions.checkArgument(handlerResponse != null,"Handler Response cannot be null");
        Preconditions.checkArgument(command != null,"Command cannot be null");
        _handlerResponse = handlerResponse;
        _command = command;
    }

    /**
     * Returns the handler response associated to this completion information
     *
     * @return the HandlerResponse
     */
    public HandlerResponse getHandlerResponse() {
        return _handlerResponse;
    }

    /**
     * Returns the Command associated to this completion information
     *
     * @return the command
     */
    public Command getCommand() {
        return _command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompletionInformation that = (CompletionInformation) o;

        if (!_command.equals(that._command)) {
            return false;
        }
        if (!_handlerResponse.equals(that._handlerResponse)) {
            return false;
        }
        //everything is okay, they are equals
        return true;
    }

    @Override
    public int hashCode() {
        int result = _handlerResponse.hashCode();
        result = 31 * result + _command.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append("[response=").append(_handlerResponse);
        sb.append("]");
        sb.append(_command);
        sb.append("]");

        return sb.toString();
    }

}

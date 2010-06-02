package edu.gemini.aspen.giapi.status;

/**
 * The Alarm State definition. Contains a Cause and Severity. 
 */
public final class AlarmState {

    private final AlarmSeverity _severity;
    private final AlarmCause _cause;
    private final String _message;

    public final static AlarmState DEFAULT = new AlarmState();

    public AlarmState(AlarmSeverity severity, AlarmCause cause, String message) {
        if (severity == null) {
            _severity = AlarmSeverity.DEFAULT;
        } else {
            _severity = severity;
        }

        if (cause == null) {
            _cause = AlarmCause.DEFAULT;
        } else {
            _cause = cause;
        }

        _message = message;
    }

    public AlarmState(AlarmSeverity severity, AlarmCause cause) {
        this(severity, cause, null);
    }

    public AlarmState() {
        this(AlarmSeverity.DEFAULT, AlarmCause.DEFAULT, null);
    }

    public AlarmSeverity getSeverity() {
        return _severity;
    }

    public AlarmCause getCause() {
        return _cause;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlarmState)) return false;

        AlarmState that = (AlarmState) o;

        if (_cause != that._cause) return false;
        if (_message != null ? !_message.equals(that._message) : that._message != null)
            return false;
        //finally, if severity is the same, they are the same
        return _severity == that._severity;

    }

    @Override
    public int hashCode() {
        int result = _severity.hashCode();
        result = 31 * result + _cause.hashCode();
        result = 31 * result + (_message != null ? _message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AlarmState{" +
                "severity=" + _severity +
                ", cause=" + _cause +
                ", message='" + _message + '\'' +
                '}';
    }
}

package edu.gemini.aspen.gmp.logging;

import org.junit.Ignore;

/**
 * Log Message class for unit testing
 */
@Ignore
public class TestLogMessage implements LogMessage {

    private Severity _severity;
    private String _message;

    public TestLogMessage(Severity severity, String msg) {
        _severity = severity;
        _message = msg;
    }

    @Override
    public Severity getSeverity() {
        return _severity;
    }

    @Override
    public String getMessage() {
        return _message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestLogMessage that = (TestLogMessage) o;

        if (_message != null ? !_message.equals(that._message) : that._message != null) return false;
        if (_severity != that._severity) return false;
        //they are equals
        return true;
    }

    @Override
    public int hashCode() {
        int result = _severity != null ? _severity.hashCode() : 0;
        result = 31 * result + (_message != null ? _message.hashCode() : 0);
        return result;
    }
}

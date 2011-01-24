package edu.gemini.aspen.gmp.logging;

/**
 * Severity of the logging information
 */
public enum Severity {

    INFO("INFO"),
    WARNING("WARNING"),
    SEVERE("SEVERE");

    private String _name;

    Severity(String name) {
        _name = name;
    }

    /**
     * This is just an utility method to help convert from an integer code to
     * a valid Severity.
     * @param code A code representing the Severity
     * @return the Severity associated to that code
     * @throws LoggingException if the code does not match any of the
     *         valid severity values.
     */
    public static Severity getSeverityByCode(int code) {
       
        switch (code) {
            case 1:
                return INFO;
            case 2:
                return WARNING;
            case 3:
                return SEVERE;
            default:
                throw new LoggingException("Invalid Severity Code: " + code);
        }
    }

    @Override
    public String toString() {
        return _name;
    }
}

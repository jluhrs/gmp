package edu.gemini.aspen.giapi.commands;

/**
 *  The different Activities supported by the GIAPI
 */
public enum Activity {
    PRESET("PRESET"),
    START("START"),
    PRESET_START("PRESET/START"),
    CANCEL("CANCEL");

    private Activity(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    /**
     * Returns the activity associated to the given name, if it exists
     * @param name string name of the activity
     * @return Activity associated to the name
     */
    public static Activity toActivity(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot call Activity.toActivity with a null argument");
        }
        if ("PRESET/START".equals(name)) {
            return PRESET_START;
        }
        return Activity.valueOf(name);
    }

    private final String _name;
}

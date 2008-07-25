package edu.gemini.aspen.gmp.commands.api;

/**
 *  The different Activities supported by the GIAPI
 */
public enum Activity {
    PRESET("PRESET"),
    START("START"),
    PRESET_START("PRESET/START"),
    CANCEL("CANCEL");

    Activity(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    private final String _name;


}

package edu.gemini.giapi.tool.parser;

/**
 * Base class for arguments. Define the key (or flag) that
 * is used to define a particular argument.
 */
public abstract class AbstractArgument implements Argument{

    private String _key;

    public AbstractArgument(String key) {
        _key = key;
    }

    public String getKey() {
        return _key;
    }
}

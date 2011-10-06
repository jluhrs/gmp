package edu.gemini.giapi.tool.parser;

/**
 * Definition of an argument
 */
public interface Argument {

    /**
     * Returns true if the argument requires a parameter.
     * For instance, to if the argument needs to be
     * specified as -arg \<val\>, then the argument 'arg' requires
     * a parameter.
     *
     * @return true if the argument requires a parameter.
     */
    boolean requireParameter();

    /**
     * Parse the parameter in the appropriate format
     * for the context defined by this argument
     * @param arg parameter to parse
     */
    void parseParameter(String arg);

    /**
     * Returns a descriptive text in case the parameter
     * can not be parsed.
     * @return Help text to describe the usage of the argument
     */
    String getInvalidArgumentMsg();

    /**
     * Return the key associated to this argument
     * @return Key associated to this argument
     */
    String getKey();
    
}

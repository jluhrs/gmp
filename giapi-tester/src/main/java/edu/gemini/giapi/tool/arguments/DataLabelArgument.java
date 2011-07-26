package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;

/**
 * A Sequence Command argument
 */
public class DataLabelArgument extends AbstractArgument {


    private String dataLabel;

    public DataLabelArgument() {
        super("dataLabel");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        dataLabel = arg;
    }

    public String getInvalidArgumentMsg() {
        return "What value? Try -dataLabel <dataLabel>";
    }

    public String getDataLabel() {
        return dataLabel;
    }
}

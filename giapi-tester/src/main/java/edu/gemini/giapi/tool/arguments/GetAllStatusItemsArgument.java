package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.FlagArgument;

/**
 * Argument to get all the registered status names
 */
public class GetAllStatusItemsArgument extends FlagArgument {
    public GetAllStatusItemsArgument() {
        super("getAllStatus");
    }
}

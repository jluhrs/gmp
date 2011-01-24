package edu.gemini.giapi.tool.parser;

/**
 * Operation to be executed using any given set of parameters
 */
public interface Operation {

    /**
     * Register the arguments needed by this operation
     * to be executed
     * @param arg The argument to be associated to this operation.
     * Multiple arguments can be set by invoking this method several times
     */
    public void setArgument(Argument arg);

    /**
     * Return true if the Operation can be executed with the
     * arguments registerd
     * @return true if the operation is ready to be executed.
     */
    public boolean isReady();

    /**
     * Execute the given operation
     * @throws Exception
     */
    public void execute() throws Exception;
}

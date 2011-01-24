package edu.gemini.giapi.tool.arguments;

import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * Argument to define how many repetitions an operation
 * will be executed
 */
public class RepetitionArgument extends AbstractArgument {

    private int _repetitions = 1;

    public RepetitionArgument() {
        super("r");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            _repetitions = Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            Util.die("Repetitions has to be an integer number. Try -r <repetitions>");
        }
        if (_repetitions <= 0) {
            Util.die("If you want to repeat, you have to specify a number bigger than 0");
        }
    }

    public String getInvalidArgumentMsg() {
        return "You have to tell me how many repetitions you want. Try -r <repetitions>";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getRepetitions() {
        return _repetitions;
    }
}

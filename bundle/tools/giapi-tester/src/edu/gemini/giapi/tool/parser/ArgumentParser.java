package edu.gemini.giapi.tool.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Parser engine to produce Argument objects
 * to be used by Operators.
 */
public class ArgumentParser {


    private String[] args;
    private int i; //current index

    private List<Operation> ops;

    private Map<String, Argument> argumentsMaps;

    public ArgumentParser(String[] arguments) {
        i = 0;
        args = arguments;
        ops = new ArrayList<Operation>();
        argumentsMaps = new HashMap<String, Argument>();
    }

    public void registerArgument(Argument op) {
        argumentsMaps.put(op.getKey(), op);
    }

    public void registerOperation(Operation op){
        if (op != null)  {
            ops.add(op);
        }
    }


    public Operation parse() {
        String opt;
        while (i < args.length) {
            opt = args[i++];
            // All options start with a
            if (opt.charAt(0) != '-')
                Util.die("I think you forgot a dash somewhere. I don't understand the argument " + opt);

            // Remove the - from the command
            String key = opt.substring(1, opt.length());
            if (key.length() == 0) {
                Util.die("I don't understand empty dashes.");
            }

            Argument argument = argumentsMaps.get(key);

            if (argument == null) {
                Util.die("I don't understand option " + opt);
            }

            if (argument.requireParameter()) {
                if (i < args.length) {
                    argument.parseParameter(args[i++]);
                } else {
                    Util.die(argument.getInvalidArgumentMsg());
                }
            } else { //A single argument option.

            }
            //set the argument in each one of the registered operations
            for(Operation op: ops) {
                op.setArgument(argument);
            }
        }

        //now, return the first operation that is ready to execute.

        for (Operation op: ops) {
            if (op.isReady()) return op;
        }
        Util.die("Sorry, what operation do you mean?");
        return null;
    }



}

package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Argument to get a Configuration
 */
public class SetStatusArgument extends AbstractArgument {

    private static final Logger LOGGER = Logger.getLogger(SetStatusArgument.class.getName());

    enum StatusType {
        STRING,
        FLOAT,
        DOUBLE,
        INTEGER;

        public static StatusType parse(String type) throws IllegalArgumentException {
            if (type == null)
                throw new IllegalArgumentException("Type: " + type + " not supported");
            return StatusType.valueOf(type.toUpperCase().trim());
        }


    }

    private StatusItem _item;

    public SetStatusArgument() {
        super("set");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            _item = _parseConfiguration(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal status item: " + arg + " (" + ex.getMessage() + ")");
        }
    }

    public String getInvalidArgumentMsg() {
        return "What configuration? Try -set <type=name=value>";
    }

    public StatusItem getStatusItem() {
        return _item;
    }

    /**
     * This method parses the configuration. This configuration is in the form
     * type=name=value, where type is one of {String, Integer, Double or Float},
     * name is the status item name, and value the value to set it.
     *
     * @param val Status item in the format type=value=name
     * @return the StatusItem constructed from the String parameter received
     * @throws IllegalArgumentException if the type is nonexistent or not supported, or the value cannot be converted to such type.
     */
    private StatusItem _parseConfiguration(String val) throws IllegalArgumentException {

        if (val == null)
            throw new IllegalArgumentException("Empty status");

        String[] arg = val.split("=");
        if (arg.length != 3)
            throw new IllegalArgumentException("Status item '" +
                    val + "' not in the form 'type=name=value'");

        String name, value;

        name = arg[1];
        value = arg[2];

        StatusItem item;

        try {

            StatusType type = StatusType.parse(arg[0]);

            switch (type) {
                case STRING:
                    item = new BasicStatus<String>(name, value);
                    break;
                case INTEGER:
                    item = new BasicStatus<Integer>(name, Integer.parseInt(value));
                    break;
                case DOUBLE:
                    item = new BasicStatus<Double>(name, Double.parseDouble(value));
                    break;
                case FLOAT:
                    item = new BasicStatus<Float>(name, Float.parseFloat(value));
                    break;
                default:
                    LOGGER.severe("This shouldn't happen. Unknown status type");
                    throw new IllegalArgumentException("Unknown status type");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(ex);
        }


        return item;
    }


}

package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.status.*;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.giapi.tool.parser.AbstractArgument;

import java.util.logging.Logger;


/**
 * Argument to get a Configuration
 */
public class SetStatusArgument extends AbstractArgument {

    private static final Logger LOGGER = Logger.getLogger(SetStatusArgument.class.getName());

    public enum StatusType {
        STRING{
            @Override
            public BasicStatus<String> getStatusItem(String name, String value){
                return new BasicStatus<String>(name, value);
            }
        },
        FLOAT{
            @Override
            public BasicStatus<Float> getStatusItem(String name, String value){
                return new BasicStatus<Float>(name, Float.parseFloat(value));
            }
        },
        DOUBLE{
            @Override
            public BasicStatus<Double> getStatusItem(String name, String value){
                return new BasicStatus<Double>(name, Double.parseDouble(value));
            }
        },
        INTEGER{
            @Override
            public BasicStatus<Integer> getStatusItem(String name, String value){
                return new BasicStatus<Integer>(name, Integer.parseInt(value));
            }
        },
        HEALTH{
            @Override
            public HealthStatus getStatusItem(String name, String value){
                return new HealthStatus(name, Health.valueOf(value));
            }
        },
        ALARM_STRING{
            @Override
            public AlarmStatus<String> getStatusItem(String name, String value, AlarmSeverity severity, AlarmCause cause, String message){
                return new AlarmStatus<String>(name, value, new AlarmState(severity, cause, message));
            }
        },
        ALARM_FLOAT{
            @Override
            public AlarmStatus<Float> getStatusItem(String name, String value, AlarmSeverity severity, AlarmCause cause, String message){
                return new AlarmStatus<Float>(name, Float.parseFloat(value), new AlarmState(severity, cause, message));
            }
        },
        ALARM_DOUBLE{
            @Override
            public AlarmStatus<Double> getStatusItem(String name, String value, AlarmSeverity severity, AlarmCause cause, String message){
                return new AlarmStatus<Double>(name, Double.parseDouble(value), new AlarmState(severity, cause, message));
            }
        },
        ALARM_INTEGER{
            @Override
            public AlarmStatus<Integer> getStatusItem(String name, String value, AlarmSeverity severity, AlarmCause cause, String message){
                return new AlarmStatus<Integer>(name, Integer.parseInt(value), new AlarmState(severity, cause, message));
            }
        };
        public StatusItem getStatusItem(String name, String value){
            //call other method with default parameters
            return getStatusItem(name,value,AlarmSeverity.ALARM_OK,AlarmCause.ALARM_CAUSE_OK, "");
        }
        public StatusItem getStatusItem(String name, String value, AlarmSeverity severity, AlarmCause cause, String message){
            //call other method
            return getStatusItem(name,value);
        }
        public static StatusType parse(String type) throws IllegalArgumentException {
            if (type == null)
                throw new IllegalArgumentException("Type: " + type + " not supported");
            return StatusType.valueOf(type.toUpperCase().trim());
        }


    }

    private String _name;

    public SetStatusArgument() {
        super("set");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        _name = arg;
    }

    public String getInvalidArgumentMsg() {
        return "What configuration? Try -set <name>";
    }

    public String getName(){
        return _name;
    }


}

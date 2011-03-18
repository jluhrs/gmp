package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.giapi.tool.parser.AbstractArgument;
import edu.gemini.giapi.tool.parser.Util;

/**
 * An argument representing the activity of a sequence command
 */
public class ActivityArgument extends AbstractArgument {
    private Activity _activity;

    public ActivityArgument() {
        super("activity");
    }

    public boolean requireParameter() {
        return true;
    }

    public void parseParameter(String arg) {
        try {
            _activity = Activity.valueOf(arg);
        } catch (IllegalArgumentException ex) {
            Util.die("Illegal activity: " + arg + ".\nOptions are: " + Util.getValues(
                    Activity.class));
        }
    }

    public String getInvalidArgumentMsg() {
        return "What activity? Try -activity <activity>";  
    }

    public Activity getActivity() {
        return _activity;
    }
}

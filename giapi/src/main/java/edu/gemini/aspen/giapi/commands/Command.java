package edu.gemini.aspen.giapi.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;

/**
 * This class encapsulates a Command sent to an instrument acting as a ParameterObject
 */
public class Command {
    private final SequenceCommand _sequenceCommand;
    private final Activity _activity;
    private final Configuration _config;
    private static final Command NO_COMMAND = new Command();

    public static Command noCommand() {
        return NO_COMMAND;
    }

    /**
     * Private constructor used only to create the noCommand constant
     */
    private Command() {
        // TODO These should not be null
        this._sequenceCommand = null;
        this._activity = null;
        this._config = emptyConfiguration();
    }

    /**
     * Builds a new immutable Command object.
     * <br>
     * Null parameters are not allowed.
     * <br>
     * Commands that need to pass a configuration like REBOOT and APPLY should use other
     * constructor
     *
     * @param sequenceCommand The Sequence sequenceCommand to send, like INIT or REBOOT
     * @param activity        The associated activities to be executed for the
     *                        specified sequence sequenceCommand, like PRESET or START
     */
    public Command(SequenceCommand sequenceCommand, Activity activity) {
        checkArgument(sequenceCommand != null, "Command cannot be null");
        checkArgument(activity != null, "Activity cannot be null");
        checkArgument(!(sequenceCommand.equals(SequenceCommand.APPLY) || sequenceCommand.equals(SequenceCommand.REBOOT) || sequenceCommand.equals(SequenceCommand.OBSERVE) || sequenceCommand.equals(SequenceCommand.ENGINEERING)), "Activity with no configuration cannot be APPLY, OBSERVE, ENGINEERING or REBOOT");

        this._sequenceCommand = sequenceCommand;
        this._activity = activity;
        this._config = emptyConfiguration();
    }

    /**
     * Builds a new immutable Command object.
     * <br>
     * Null parameters are not allowed.
     *
     * @param sequenceCommand The Sequence sequenceCommand to send, like INIT or REBOOT
     * @param activity        The associated activities to be executed for the
     *                        specified sequence sequenceCommand, like PRESET or START
     * @param config          the configuration that will be sent along with the
     *                        sequence sequenceCommand
     */
    public Command(SequenceCommand sequenceCommand, Activity activity, Configuration config) {
        checkArgument(sequenceCommand != null, "Command cannot be null");
        checkArgument(activity != null, "Activity cannot be null");
        checkArgument(config != null, "Configuration cannot be null, use emptyConfiguration instead");
        String param=getParam(sequenceCommand);
        if(param!=null){
            checkArgument(config.getValue(ConfigPath.configPath(param))!=null, "'"+sequenceCommand+"' command requires a mandatory '"+param+"' parameter");
        }

        this._sequenceCommand = sequenceCommand;
        this._activity = activity;
        this._config = config;
    }

    private String getParam(SequenceCommand sc) {
        if (sc.equals(SequenceCommand.ENGINEERING)) {
            return "COMMAND_NAME";
        } else if (sc.equals(SequenceCommand.REBOOT)) {
            return "REBOOT_OPT";
        } else if (sc.equals(SequenceCommand.OBSERVE)) {
            return "DATA_LABEL";
        } else{
            return null;
        }
    }

    public boolean isApply() {
        return _sequenceCommand == SequenceCommand.APPLY;
    }

    public SequenceCommand getSequenceCommand() {
        return _sequenceCommand;
    }

    public Activity getActivity() {
        return _activity;
    }

    public Configuration getConfiguration() {
        return _config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Command command = (Command) o;

        if (_activity != command._activity) {
            return false;
        }
        if (!_config.equals(command._config)) {
            return false;
        }
        if (_sequenceCommand != command._sequenceCommand) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _sequenceCommand.hashCode();
        result = 31 * result + _activity.hashCode();
        result = 31 * result + _config.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "[command=" + _sequenceCommand +
                "][activity=" + _activity +
                "][" + _config +
                "]";
    }

}

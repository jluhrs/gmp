package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.apache.felix.service.command.Descriptor;

@Component
@Instantiate
@Provides
public class GmpCommandsProxy {
    private static final String ACTIVITY_DESCRIPTION = "activity: START, PRESET, START_PRESET, CANCEL";
    
    @ServiceProperty(name = "osgi.command.scope", value = "gmp")
    private final String SCOPE = "gmp";
    @ServiceProperty(name = "osgi.command.function")
    private final String[] FUNCTIONS = new String[]{"command", "test", "init", "park", "datum", "verify", "endVerify", "guide", "endGuide", "apply"};

    private final CommandSender commandSender;
    private final GenericCompletionListener completionListener = new GenericCompletionListener();

    public GmpCommandsProxy(@Requires CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Descriptor("Issues a park command over GMP")
    public void park(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.PARK, activityArg));
    }

    private Command buildCommand(SequenceCommand sequenceCommand, String activityArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());

        return new Command(sequenceCommand, activity);
    }

    private void issueCommand(Command command) {
        HandlerResponse initialResponse = commandSender.sendCommand(command, completionListener);
        completionListener.printCompletion(initialResponse, command);
    }

    @Descriptor("Issues a test command over GMP")
    public void test(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.TEST, activityArg));
    }

    @Descriptor("Issues an init command over GMP")
    public void init(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.INIT, activityArg));
    }

    @Descriptor("Issues a datum command over GMP")
    public void datum(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.DATUM, activityArg));
    }

    @Descriptor("Issues a verify command over GMP")
    public void verify(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.VERIFY, activityArg));
    }

    @Descriptor("Issues an endVerify command over GMP")
    public void endVerify(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.END_VERIFY, activityArg));
    }

    @Descriptor("Issues a guide command over GMP")
    public void guide(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.GUIDE, activityArg));
    }

    @Descriptor("Issues an endGuide command over GMP")
    public void endGuide(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        issueCommand(buildCommand(SequenceCommand.END_GUIDE, activityArg));
    }

    @Descriptor("Issues an apply command over GMP")
    public void apply(@Descriptor(ACTIVITY_DESCRIPTION) String activityArg,
                      @Descriptor("configuration: {path=value, path=value}") String configurationArg) {
        Command command = buildCommand(SequenceCommand.APPLY, activityArg, configurationArg);

        issueCommand(command);
    }

    private Command buildCommand(SequenceCommand sequenceCommand, String activityArg, String configurationArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());
        Configuration configuration = new ConfigurationParser(configurationArg).parse();

        return new Command(sequenceCommand, activity, configuration);
    }

    @Descriptor("Issues an generic command with no configuration over GMP")
    public void command(@Descriptor("command: TEST, INIT, DATUM, PARK") String commandArg,
                        @Descriptor(ACTIVITY_DESCRIPTION) String activityArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());
        SequenceCommand sequenceCommand = SequenceCommand.valueOf(commandArg.toUpperCase());

        Command command = new Command(sequenceCommand, activity);

        issueCommand(command);
    }

    @Descriptor("Issues an generic command with no configuration over GMP")
    public void command(@Descriptor("command: TEST, INIT, DATUM, PARK") String commandArg,
                        @Descriptor(ACTIVITY_DESCRIPTION) String activityArg,
                        @Descriptor("configuration: {path:value,path:value}") String configurationArg) {
    }

    private static class GenericCompletionListener implements CompletionListener {
        @Override
        public void onHandlerResponse(HandlerResponse response, Command command) {
            printCompletion(response, command);
        }

        public void printCompletion(HandlerResponse response, Command command) {
            System.out.println(command + " -> " + response);
        }
    }
}

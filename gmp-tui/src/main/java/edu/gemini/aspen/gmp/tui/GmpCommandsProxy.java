package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
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
    @ServiceProperty(name = "osgi.command.scope", value = "gmp")
    private final String SCOPE = "gmp";
    @ServiceProperty(name = "osgi.command.function")
    private final String[] FUNCTIONS = new String[]{"command", "park", "apply"};

    private final CommandSender commandSender;
    private GenericCompletionListener completionListener = new GenericCompletionListener();

    public GmpCommandsProxy(@Requires CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Descriptor("Issues a park command over GMP")
    public void park(@Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());

        Command command = new Command(SequenceCommand.PARK, activity);

        issueCommand(command);
    }

    private void issueCommand(Command command) {
        HandlerResponse initialResponse = commandSender.sendCommand(command, completionListener);
        completionListener.printCompletion(initialResponse, command);
    }

    @Descriptor("Issues an apply command over GMP")
    public void apply(@Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg,
                      @Descriptor("configuration: {path:value,path:value}") String configurationArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());

        Command command = new Command(SequenceCommand.PARK, activity);

        issueCommand(command);
    }

    @Descriptor("Issues an generic command with no configuration over GMP")
    public void command(@Descriptor("command: TEST, INIT, DATUM, PARK") String commandArg,
                      @Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg) {
        Activity activity = Activity.valueOf(activityArg.toUpperCase());
        SequenceCommand sequenceCommand = SequenceCommand.valueOf(commandArg.toUpperCase());

        Command command = new Command(sequenceCommand, activity);

        issueCommand(command);
    }

@Descriptor("Issues an generic command with no configuration over GMP")
    public void command(@Descriptor("command: TEST, INIT, DATUM, PARK") String commandArg,
                      @Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg,
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

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
    private final String[] FUNCTIONS = new String[]{"park", "apply"};

    private final CommandSender commandSender;

    public GmpCommandsProxy(@Requires CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    @Descriptor("Issues a park command over GMP")
    public void park(@Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg) {
        System.out.println("Call activity on " + activityArg + " " + commandSender);
        Activity activity = Activity.valueOf(activityArg);

        Command command = new Command(SequenceCommand.PARK, activity);

        HandlerResponse initialResponse = commandSender.sendCommand(command, new CompletionListener() {
            @Override
            public void onHandlerResponse(HandlerResponse response, Command command) {
                System.out.println(response);
            }
        });
        System.out.println(initialResponse);
    }

    @Descriptor("Issues an apply command over GMP")
    public void apply(@Descriptor("activity: START, PRESET, START_PRESET, CANCEL") String activityArg,
                      @Descriptor("configuration: {path:value,path:value}") String configurationArg) {
        System.out.println("Call activity on " + activityArg + " " + commandSender);
        Activity activity = Activity.valueOf(activityArg);

        Command command = new Command(SequenceCommand.PARK, activity);

        HandlerResponse initialResponse = commandSender.sendCommand(command, new CompletionListener() {
            @Override
            public void onHandlerResponse(HandlerResponse response, Command command) {
                System.out.println(response);
            }
        });
        System.out.println(initialResponse);
    }
}

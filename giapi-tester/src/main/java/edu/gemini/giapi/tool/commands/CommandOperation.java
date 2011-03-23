package edu.gemini.giapi.tool.commands;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.gmp.commands.jms.client.CommandSenderClient;
import edu.gemini.giapi.tool.arguments.ActivityArgument;
import edu.gemini.giapi.tool.arguments.ConfigArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.RepetitionArgument;
import edu.gemini.giapi.tool.arguments.SequenceCommandArgument;
import edu.gemini.giapi.tool.arguments.TimeoutArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;

import java.util.logging.Logger;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;

/**
 * Provides the operation to send sequence commands to the GMP
 */
public class CommandOperation implements Operation {
    private static final Logger LOG = Logger.getLogger(
            CommandOperation.class.getName());

    private SequenceCommandArgument _scArgument;

    private ActivityArgument _activityArgument;

    private ConfigArgument _configArgument;

    private String host = "localhost";

    private int repetitions = 1;
    private long timeout = 25000;

    public void execute() throws Exception {
        String url = "tcp://" + host + ":61616";

        ActiveMQJmsProvider provider = new ActiveMQJmsProvider(url);
        provider.startConnection();
        CommandSenderClient senderClient = new CommandSenderClient(provider);

        Configuration config = (_configArgument != null) ? _configArgument.getConfiguration() : emptyConfiguration();

        Command command = new Command(
                _scArgument.getSequenceCommand(),
                _activityArgument.getActivity(),
                config);

        for (int x = 0; x < repetitions; x++) {
            WaitingCompletionListener listener = new WaitingCompletionListener();
            HandlerResponse response = senderClient.sendCommand(command, listener);

            LOG.info("Response Received: " + response);

            if (response == HandlerResponse.STARTED) {
                //now, wait for the answer, synchronously
                CompletionInformation completionInformation
                        = listener.waitForResponse(timeout);
                LOG.info("Completion Information: " + completionInformation);
            }
        }

    }

    public void setArgument(Argument arg) {
        if (arg instanceof SequenceCommandArgument) {
            _scArgument = (SequenceCommandArgument) arg;
        } else if (arg instanceof ActivityArgument) {
            _activityArgument = (ActivityArgument) arg;
        } else if (arg instanceof ConfigArgument) {
            _configArgument = (ConfigArgument) arg;
        } else if (arg instanceof TimeoutArgument) {
            timeout = ((TimeoutArgument) arg).getTimeout();
        } else if (arg instanceof HostArgument) {
            host = ((HostArgument) arg).getHost();
        } else if (arg instanceof RepetitionArgument) {
            repetitions = ((RepetitionArgument) arg).getRepetitions();
        }
    }

    public boolean isReady() {
        return (_scArgument != null && _activityArgument != null);
    }

}

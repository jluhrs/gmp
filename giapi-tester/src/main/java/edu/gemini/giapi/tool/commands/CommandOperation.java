package edu.gemini.giapi.tool.commands;

import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapitestsupport.TesterException;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.*;
import edu.gemini.aspen.giapi.commands.Configuration;

import java.util.logging.Logger;

/**
 * Provides the operation to send sequence commands to the GMP
 */
public class CommandOperation implements Operation {


    private static final Logger LOG = Logger.getLogger(
            CommandOperation.class.getName());

    private SequenceCommandArgument _sc;

    private ActivityArgument _activity;

    private ConfigArgument _config;

    private String host = "localhost";

    private int repetitions = 1;
    private long timeout = 0;


    public void execute() throws Exception {
        BrokerConnection connection = new BrokerConnection(
                "tcp://" + host + ":61616");
        try {
            connection.start();
            CommandSender sender = new CommandSender(connection);

            Configuration config = (_config != null) ? _config.getConfiguration() : null;
                    

            for (int x = 0; x < repetitions; x++) {
                HandlerResponse response = sender.send(_sc.getSequenceCommand(),
                                                       _activity.getActivity(), 
                                                       config);

                System.out.println("Response Received: " + response);

                if (response == HandlerResponse.STARTED) {
                    //now, wait for the answer, synchronously
                    CompletionInformation info = sender.receiveCompletionInformation(
                            timeout);
                    System.out.println("Completion Information: " + info);
                }
            }
        } catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            connection.stop();
        }
    }

    public void setArgument(Argument arg) {

        if (arg instanceof SequenceCommandArgument) {
            _sc = (SequenceCommandArgument)arg;
        } else if (arg instanceof ActivityArgument) {
            _activity = (ActivityArgument)arg;
        } else if (arg instanceof ConfigArgument) {
            _config = (ConfigArgument)arg;
        } else if (arg instanceof TimeoutArgument) {
            timeout = ((TimeoutArgument)arg).getTimeout();
        } else if (arg instanceof HostArgument) {
            host = ((HostArgument)arg).getHost();
        } else if (arg instanceof RepetitionArgument) {
            repetitions = ((RepetitionArgument)arg).getRepetitions();
        }

    }

    public boolean isReady() {

        return ((_sc != null) && (_activity != null));

    }

}

package edu.gemini.giapi.tool.obsevents;

import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CompletionInformation;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.util.jms.ObsEventSender;
import edu.gemini.aspen.gmp.commands.jms.client.CommandSenderClient;
import edu.gemini.giapi.tool.arguments.*;
import edu.gemini.giapi.tool.commands.WaitingCompletionListener;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Logger;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;

/**
 * Provides the operation to send sequence commands to the GMP
 */
public class SendObsEventOperation implements Operation {
    private final Logger LOG = Logger.getLogger(
            this.getClass().getName());

    private SendObsEventArgument _obsEventArgument;

    private ObsEventSender senderClient;

    private String host = "localhost";
    private String dataLabel = "";

    private long timeout = Long.MAX_VALUE; // Wait forever
    private long repetitions = 1;

    public SendObsEventOperation() {
    }


    @Override
    public int execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + host + ":61616");
        senderClient = new ObsEventSender("GIAPI Tester");

        try {
            senderClient.startJms(provider);

            for (int x = 0; x < repetitions; x++) {
                senderClient.send(_obsEventArgument.getObservationEvent(), new DataLabel(dataLabel));
            }

        } catch (JMSException ex) {
            LOG.severe("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            senderClient.stopJms();
        }

        return 0;
    }

    @Override
    public void setArgument(Argument arg) {
        if (arg instanceof SendObsEventArgument) {
            _obsEventArgument = (SendObsEventArgument) arg;
        } else if (arg instanceof HostArgument) {
            host = ((HostArgument) arg).getHost();
        } else if (arg instanceof DataLabelArgument) {
            dataLabel = ((DataLabelArgument) arg).getDataLabel();
        } else if (arg instanceof RepetitionArgument) {
            repetitions = ((RepetitionArgument) arg).getRepetitions();
        }
    }

    @Override
    public boolean isReady() {
        return (_obsEventArgument != null);
    }

}

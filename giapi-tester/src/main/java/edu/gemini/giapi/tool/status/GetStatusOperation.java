package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.status.StatusGetter;
import edu.gemini.giapi.tool.arguments.GetStatusArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The operation to get the last value of a status item from the GMP.
 */
public class GetStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(GetStatusOperation.class.getName());

    private String _statusName;

    private String _host = "localhost";

    public void setArgument(Argument arg) {
        if (arg instanceof GetStatusArgument) {
            _statusName = ((GetStatusArgument) arg).getStatusName();
        }
        if (arg instanceof HostArgument) {
            _host = ((HostArgument) arg).getHost();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    public int execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter("GIAPI Tester");

        try {
            getter.startJms(provider);

            StatusItem item = getter.getStatusItem(_statusName);

            if (item != null) {
                System.out.println("Status Value: " + item);
            } else {
                System.out.println("No information found for " + _statusName);
            }

            getter.stopJms();

        } catch (JMSException ex) {
            LOG.log(Level.WARNING,"Problem on GIAPI tester",ex);
        }
        return 0;
    }
}

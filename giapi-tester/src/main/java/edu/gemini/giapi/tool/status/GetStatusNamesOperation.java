package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.util.jms.status.StatusGetter;
import edu.gemini.giapi.tool.arguments.GetStatusNamesArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The operation to get the last value of a status item from the GMP.
 */
public class GetStatusNamesOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(GetStatusNamesOperation.class.getName());

    private String _host = "localhost";

    private boolean ready = false;

    public void setArgument(Argument arg) {
        if (arg instanceof GetStatusNamesArgument) {
            ready = true;
        }
        if (arg instanceof HostArgument) {
            _host = ((HostArgument) arg).getHost();
        }
    }

    public boolean isReady() {
        return ready;
    }

    public int execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter("GIAPI Tester");

        try {
            getter.startJms(provider);

            Set<String> names = getter.getStatusNames();

            if (names != null) {
                System.out.println("Registered status names:");
                for (String name : names) {
                    System.out.println(name);
                }
            } else {
                System.out.println("Couldn't retrieve status names");
            }


        } catch (JMSException ex) {
            LOG.log(Level.WARNING,"Problem on GIAPI tester",ex);
        } finally {
            getter.stopJms();
        }
        return 0;
    }
}

package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.SetStatusArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * The operation to get the last value of a status item from the GMP.
 */
public class SetStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(SetStatusOperation.class.getName());

    private StatusItem _statusItem;

    private String _host = "localhost";

    public void setArgument(Argument arg) {
        if (arg instanceof SetStatusArgument) {
            _statusItem = ((SetStatusArgument)arg).getStatusItem();
        } if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        }
    }

    public boolean isReady() {
        return _statusItem != null;
    }

    public void execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusSetter setter = new StatusSetter(_statusItem.getName());

        try {
            setter.startJms(provider);
            
            setter.setStatusItem(_statusItem);



        }  catch (JMSException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally{
             setter.stopJms();
        }
    }
}

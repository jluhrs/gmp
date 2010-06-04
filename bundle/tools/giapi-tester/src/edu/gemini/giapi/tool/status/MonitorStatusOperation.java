package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.statusservice.jms.JmsStatusListener;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.MonitorStatusArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Logger;

/**
 * Monitor a status item.
 */
public class MonitorStatusOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(MonitorStatusOperation.class.getName());

    private String _statusName;

    private String _host = "localhost";

    private class StatusMonitor implements StatusHandler {
        public String getName() {
            return "Status Monitor";
        }

        public void update(StatusItem item) {
            System.out.println("Status value: " + item);
        }
    }

    public void setArgument(Argument arg) {
        if (arg instanceof MonitorStatusArgument) {
            _statusName = ((MonitorStatusArgument)arg).getStatusName();
        } if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    public void execute() throws Exception {

        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        StatusGetter getter = new StatusGetter();

        StatusMonitor monitor = new StatusMonitor();

        JmsStatusListener listener = new JmsStatusListener(monitor);

        BaseMessageConsumer consumer = new BaseMessageConsumer(
                "Status Test Client",
                new DestinationData(GmpKeys.GMP_STATUS_DESTINATION_PREFIX + _statusName,
                        DestinationType.TOPIC),
                listener
        );

        try {
            
            getter.startJms(provider);
            
            StatusItem item = getter.getStatusItem(_statusName);
            
            monitor.update(item);

            getter.stopJms();

            consumer.startJms(provider);
            
        } catch (JMSException e) {
            LOG.warning("Problem on GIAPI tester: " + e.getMessage());
        }


        
    }
}

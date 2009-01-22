package edu.gemini.giapi.tool.status;

import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.MonitorStatusArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;
import edu.gemini.aspen.gmp.status.api.StatusHandler;
import edu.gemini.aspen.gmp.status.api.StatusItem;

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
            System.out.println("Value changed: " + item);
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
        //instantiate the status reader, and register the handler

        BrokerConnection connection = new BrokerConnection(
                "tcp://" + _host + ":61616");

        try {
            connection.start();

            StatusReader reader = new StatusReader(connection, new StatusMonitor(), _statusName);
            reader.start();
            
        }  catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            connection.stop();
        }
        
    }
}

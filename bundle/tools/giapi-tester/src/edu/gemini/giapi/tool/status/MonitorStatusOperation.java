package edu.gemini.giapi.tool.status;

import edu.gemini.aspen.gmp.status.StatusHandler;
import edu.gemini.aspen.gmp.status.StatusItem;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.MonitorStatusArgument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;

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
        //instantiate the status reader, and register the handler

        BrokerConnection connection = new BrokerConnection(
                "tcp://" + _host + ":61616");

        StatusGetter getter = null;
        try {
            connection.start();

            getter = new StatusGetter(connection);

            StatusItem item = getter.getStatusItem(_statusName);

            if (item == null) {
                System.out.println("No information found for " + _statusName);
                return;
            }

            StatusMonitor monitor = new StatusMonitor();

            monitor.update(item);
            //now we know the item exists in the GMP database. Keeps monitoring

            StatusReader reader = new StatusReader(connection, monitor, _statusName);
            reader.start();
            
        }  catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            connection.stop();
            if (getter != null) getter.stop();
        }
        
    }
}

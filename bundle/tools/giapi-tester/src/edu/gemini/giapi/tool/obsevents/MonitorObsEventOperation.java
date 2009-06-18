package edu.gemini.giapi.tool.obsevents;

import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.MonitorObsEventArgument;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;

import java.util.logging.Logger;

/**
 * An operation to receive Observation Events. 
 */
public class MonitorObsEventOperation implements Operation {


    private static final Logger LOG = Logger.getLogger(MonitorObsEventOperation.class.getName());

    private String _host = "localhost";

    public boolean isReady = false;

    public void setArgument(Argument arg) {
        if (arg instanceof MonitorObsEventArgument) {
            isReady = true;
        } else if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void execute() throws Exception {
        BrokerConnection connection = new BrokerConnection(
                "tcp://" + _host + ":61616");

        ObsEventMonitor monitor= null;

        try {
            connection.start();
            monitor = new ObsEventMonitor(connection);
            monitor.start();
        } catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            connection.stop();
            if (monitor != null) monitor.stop();
        }

        


    }
}

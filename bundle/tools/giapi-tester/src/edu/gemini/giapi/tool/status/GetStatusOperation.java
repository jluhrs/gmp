package edu.gemini.giapi.tool.status;

import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.GetStatusArgument;
import edu.gemini.giapi.tool.jms.BrokerConnection;
import edu.gemini.giapi.tool.TesterException;
import edu.gemini.aspen.gmp.status.api.StatusItem;

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
            _statusName = ((GetStatusArgument)arg).getStatusName();
        } if (arg instanceof HostArgument) {
            _host = ((HostArgument)arg).getHost();
        }
    }

    public boolean isReady() {
        return _statusName != null;
    }

    public void execute() throws Exception {
        BrokerConnection connection = new BrokerConnection(
                "tcp://" + _host + ":61616");

        StatusGetter getter = null;
        try {
            connection.start();

            getter = new StatusGetter(connection);

            StatusItem item = getter.getStatusItem(_statusName);

            if (item != null) {
                System.out.println("Status Value: " + item);
            } else {
                System.out.println("No information found for " + _statusName);
            }

        }  catch (TesterException ex) {
            LOG.warning("Problem on GIAPI tester: " + ex.getMessage());
        } finally {
            connection.stop();
            if (getter != null) getter.stop();
        }

    }
}

package edu.gemini.giapi.tool.fileevents;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.fileevents.FileEventAction;
import edu.gemini.aspen.giapi.data.fileevents.jms.JmsFileEventsListener;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.MonitorFileEventsArgument;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;

import javax.jms.JMSException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  Operation to receive file events
 */
public class MonitorFileEventsOperation implements Operation {

    private static final Logger LOG = Logger.getLogger(MonitorFileEventsOperation.class.getName());

    private String _host = "localhost";

    private boolean _isReady = false;

    public void setArgument(Argument arg) {
        if (arg instanceof MonitorFileEventsArgument) {
            _isReady = true;
        } else if (arg instanceof HostArgument) {
            _host = ((HostArgument) arg).getHost();
        }

    }

    public boolean isReady() {
        return _isReady;
    }

    public int execute() throws Exception {
        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        JmsFileEventsListener listener = new JmsFileEventsListener(new TestFileEventsAction());

        BaseMessageConsumer consumer = new BaseMessageConsumer(
                "File Event Test Client",
                new DestinationData(JmsFileEventsListener.TOPIC_NAME,
                        DestinationType.TOPIC),
                listener
        );

        try {
            consumer.startJms(provider);
            Thread.sleep(Long.MAX_VALUE);//wait for events until somebody quits the application
        } catch (JMSException e) {
            e.printStackTrace();
            LOG.log(Level.WARNING, "Problem on GIAPI tester", e);
        }
        return 0;
    }


    private final class TestFileEventsAction implements FileEventAction  {
        public void onAncillaryFileEvent(String filename, DataLabel dataLabel) {
            System.out.println("Ancillary File Event    [" + dataLabel.getName() + "/" + filename + "]");
        }

        public void onIntermediateFileEvent(String filename, DataLabel dataLabel, String hint) {
            System.out.println("Intermediate File Event [" + dataLabel.getName() + "/" + filename + "/" + hint + "]");
        }
    }
}

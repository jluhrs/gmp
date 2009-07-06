package edu.gemini.giapi.tool.obsevents;

import edu.gemini.giapi.tool.parser.Operation;
import edu.gemini.giapi.tool.parser.Argument;
import edu.gemini.giapi.tool.arguments.HostArgument;
import edu.gemini.giapi.tool.arguments.MonitorObsEventArgument;
import edu.gemini.aspen.giapi.data.obsevents.jms.JmsObservationEventListener;
import edu.gemini.aspen.gmp.data.ObservationEventHandler;
import edu.gemini.aspen.gmp.data.ObservationEvent;
import edu.gemini.aspen.gmp.data.Dataset;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.activemq.broker.ActiveMQJmsProvider;

import javax.jms.JMSException;
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
            _host = ((HostArgument) arg).getHost();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public void execute() throws Exception {

        JmsProvider provider = new ActiveMQJmsProvider("tcp://" + _host + ":61616");

        JmsObservationEventListener listener = new JmsObservationEventListener(new TestObsEventHandler());

        BaseMessageConsumer consumer = new BaseMessageConsumer(
                "Observation Event Test Client",
                JmsObservationEventListener.TOPIC_NAME,
                listener
        );

        try {
            consumer.startJms(provider);
        } catch (JMSException e) {
            LOG.warning("Problem on GIAPI tester: " + e.getMessage());   
        }
    }

    public class TestObsEventHandler implements ObservationEventHandler {
        public void onObservationEvent(ObservationEvent event, Dataset dataset) {
            System.out.println("[" + dataset.getName() + "/" + event + "]");
        }
    }
}

package edu.gemini.aspen.gmp.logging;

import edu.gemini.aspen.gmp.logging.jms.LoggingListener;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

public class LoggingMessageConsumer extends BaseMessageConsumer {
    public LoggingMessageConsumer() {
        super("Logging Message Consumer",
                new DestinationData(LoggingListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new LoggingListener(new DefaultLogProcessor()));
    }
}

package edu.gemini.aspen.gmp.logging;

import edu.gemini.aspen.gmp.logging.jms.LoggingListener;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

@Component
@Instantiate
@Provides
public class LoggingMessageConsumer extends BaseMessageConsumer {
    public LoggingMessageConsumer() {
        super("Logging Message Consumer",
                new DestinationData(LoggingListener.DESTINATION_NAME,
                        DestinationType.TOPIC),
                new LoggingListener(new DefaultLogProcessor()));
    }
}

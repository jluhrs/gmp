package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsArtifact;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

@Component
@Instantiate
@Provides(specifications = JmsArtifact.class)
public class CommandMessagesConsumer extends BaseMessageConsumer {
    private static final String CONSUMER_NAME = "Gateway Command Consumer";

    public CommandMessagesConsumer(@Requires CommandMessagesBridge bridge) {
        super(CONSUMER_NAME,
                new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC),
                bridge);
    }
}

package edu.gemini.aspen.gmp.commands.jms.clientbridge;

import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;

public class CommandMessagesConsumer extends BaseMessageConsumer {
    private static final String CONSUMER_NAME = "Gateway Command Consumer";

    public CommandMessagesConsumer(CommandMessagesBridge bridge) {
        super(CONSUMER_NAME,
                new DestinationData(JmsKeys.GW_COMMAND_TOPIC, DestinationType.TOPIC),
                bridge);
    }
}

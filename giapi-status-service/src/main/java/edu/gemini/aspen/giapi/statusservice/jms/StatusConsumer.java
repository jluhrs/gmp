package edu.gemini.aspen.giapi.statusservice.jms;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.*;

/**
 * JMS Message consumer to receive status items.
 */
public class StatusConsumer extends BaseMessageConsumer {

    public StatusConsumer(String clientName, String item) {
        super(clientName,
                new DestinationData(JmsKeys.GMP_STATUS_DESTINATION_PREFIX + item,
                        DestinationType.TOPIC));
    }

}


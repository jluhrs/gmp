package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.gmp.services.core.JmsService;
import edu.gemini.aspen.gmp.services.core.ServiceType;
import edu.gemini.aspen.gmp.util.jms.GmpKeys;

import javax.jms.*;
import java.util.logging.Logger;

/**
 *
 */
public class PropertyService extends JmsService {

    private static final Logger LOG = Logger.getLogger(PropertyService.class.getName());

    private PropertyHolder _properties;

    public PropertyService(PropertyHolder holder) {
        _properties = holder;
    }

    public void process(MapMessage message) throws JMSException {

        if (message == null) return;

        String key = message.getString(GmpKeys.GMP_UTIL_PROPERTY);

        String reply = _properties.getProperty(key);

        Destination destination = message.getJMSReplyTo();
        if (destination == null) {
            LOG.info("Invalid destination received. Can't reply to request");
            return;
        }

        MessageProducer replyProducer = session.createProducer(destination);

        Message replyMessage = session.createTextMessage(reply);
        replyProducer.send(replyMessage);
    }

    public ServiceType getType() {
        return ServiceType.PROPERTY_SERVICE;
    }
}

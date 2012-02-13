package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.services.PropertyHolder;
import edu.gemini.aspen.gmp.services.core.ServiceException;
import edu.gemini.aspen.gmp.services.core.ServiceType;
import edu.gemini.aspen.gmp.services.jms.JmsService;
import edu.gemini.aspen.gmp.services.jms.JmsServiceRequest;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;

import javax.jms.*;
import java.util.logging.Logger;

@Component
@Instantiate
@Provides
public class PropertyService extends JmsService {
    private static final Logger LOG = Logger.getLogger(PropertyService.class.getName());

    private PropertyHolder _properties;

    public PropertyService(PropertyHolder holder) {
        _properties = holder;
        LOG.info("Properties service started with properties: " + _properties);
    }

    public void process(JmsServiceRequest jmsRequest) throws ServiceException {
        MapMessage msg = jmsRequest.getMessage();
        if (msg == null) return;

        try {
            String key = msg.getString(JmsKeys.GMP_UTIL_PROPERTY);
            String reply = _properties.getProperty(key);

            Destination destination = msg.getJMSReplyTo();
            if (destination == null) {
                LOG.info("Invalid destination received. Can't reply to request");
                return;
            }

            MessageProducer replyProducer = session.createProducer(destination);

            Message replyMessage = session.createTextMessage(reply);
            replyProducer.send(replyMessage);
            replyProducer.close();
        } catch (JMSException e) {
            throw new ServiceException(e);
        }
    }

    public ServiceType getType() {
        return ServiceType.PROPERTY_SERVICE;
    }
}

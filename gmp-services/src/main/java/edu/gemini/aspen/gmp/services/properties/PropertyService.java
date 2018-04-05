package edu.gemini.aspen.gmp.services.properties;

import com.google.common.base.Preconditions;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.services.PropertyHolder;
import edu.gemini.aspen.gmp.services.core.ServiceException;
import edu.gemini.aspen.gmp.services.core.ServiceType;
import edu.gemini.aspen.gmp.services.jms.JmsService;
import edu.gemini.aspen.gmp.services.jms.JmsServiceRequest;

import javax.jms.*;
import java.util.logging.Logger;

public class PropertyService extends JmsService {
    private static final Logger LOG = Logger.getLogger(PropertyService.class.getName());

    private PropertyHolder _properties;

    public PropertyService(PropertyHolder holder) {
        _properties = holder;
        LOG.info("Properties service started with properties: " + _properties);
    }

    public void process(JmsServiceRequest jmsRequest) throws ServiceException {
        Preconditions.checkState(_session != null, "Session should have been initialized");
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

            MessageProducer replyProducer = _session.createProducer(destination);

            Message replyMessage = _session.createTextMessage(reply);
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

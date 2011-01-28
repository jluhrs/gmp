package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.logging.Logger;

/**
 * Implementation of
 */
@Component(name = "Handlers State Service")
@Instantiate
@Provides
public class HandlersStateServiceImpl implements HandlersStateService {
    private static final Logger LOG = Logger.getLogger(HandlersStateService.class.getName());

    @Requires
    private JmsProvider _provider;

    @Override
    public boolean isConfigurationHandled(Configuration path) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
 * JMS Message consumer to receive status items.
 */
private class StatusConsumer extends BaseMessageConsumer {

    public StatusConsumer(String clientName) {
        super(clientName,
                new DestinationData("ActiveMQ.Advisory.*",
                        DestinationType.TOPIC));
        setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                LOG.info("AAAAAAAAAAAAAAAAAA" + message.toString());

            }
        });
    }




}



    public boolean isPathHandled(ConfigPath path) {
        System.out.println(path);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Validate
    public void validated() {
        LOG.info("Starting the HandlersState Service " + _provider);
        try {
            new JMXConsumerStateHolder();
            new StatusConsumer("ME").startJms(_provider);
        } catch (JMSException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
//        _provider.getConnectionFactory().createConnection().
    }

    @Invalidate
    public void invalidate() {
        
    }

    public void advisoryMessage(String message) {
        System.out.println(message);
    }
}

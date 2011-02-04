package edu.gemini.aspen.gmp.handlersstate.impl;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.gmp.handlersstate.HandlersStateService;
import edu.gemini.jms.api.JmsProvider;
import org.apache.felix.ipojo.annotations.*;

import javax.jms.JMSException;
import java.util.Set;
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

    private JMXConsumerStateHolder jmxConsumerStateHolder;
    private AdvisoriesConsumerStateHolder advisoriesConsumerStateHolder;
    private volatile boolean started = false;

    @Override
    public boolean isConfigurationHandled(Configuration path) {
        Set<ConfigPath> pathKeys = path.getKeys();
//        for (ConfigPath p:pathKeys) {
//            System.out.println(p.getName());
//            System.out.println(p.getParent());
//            System.out.println(p.getReferencedName());
//            System.out.println(p.split()[0]);
//        }


        return false;
    }

    @Validate
    public void validated() {
        if (!started) {
            LOG.info("Starting the HandlersState Service " + _provider);
            jmxConsumerStateHolder = new JMXConsumerStateHolder();
            advisoriesConsumerStateHolder = new AdvisoriesConsumerStateHolder();
            try {
                advisoriesConsumerStateHolder.startJms(_provider);
            } catch (JMSException e) {
                LOG.severe("Not possible to start a consumer state holder using advisory messages");
            }
            started = true;
        }
    }

    @Invalidate
    public void invalidate() {
        if (started) {
            started = false;
            advisoriesConsumerStateHolder.stopJms();
        }
    }

}

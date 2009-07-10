package edu.gemini.aspen.gmp.commands.osgi;

import org.osgi.util.tracker.ServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import edu.gemini.jms.api.JmsProvider;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.aspen.gmp.commands.api.CommandSender;
import edu.gemini.aspen.gmp.commands.api.CommandUpdater;
import edu.gemini.aspen.gmp.commands.jms.JMSCompletionInfoListener;
import edu.gemini.aspen.gmp.commands.jms.JMSActionMessageProducer;
import edu.gemini.aspen.gmp.commands.jms.ActionSenderStrategy;
import edu.gemini.aspen.gmp.commands.impl.CommandSenderImpl;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.ActionManager;

import javax.jms.JMSException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * This class tracks for the presence of a JMS Provider service in the
 * OSGi framework. Once it founds the provider, initializes the
 * GMP Service.
 */
public class JmsProviderTracker extends ServiceTracker {

    private static final Logger LOG = Logger.getLogger(JmsProviderTracker.class.getName());


    BaseMessageConsumer _messageConsumer;

    private JMSActionMessageProducer _actionMessageProducer;

    private ActionManager _actionManager = null;

    ServiceRegistration _registration;

    public JmsProviderTracker(BundleContext ctx, ActionManager manager) {
        super(ctx, JmsProvider.class.getName(), null);
        _actionManager = manager;
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        LOG.info("Adding JMS Service provider");
        JmsProvider provider = (JmsProvider) context.getService(serviceReference);
        //start the action Message producer
        _actionMessageProducer = new JMSActionMessageProducer(provider);

        LOG.info("Starting Command Sender bundle");
        CommandSender commandSender = new CommandSenderImpl(
                new ActionSenderStrategy(_actionMessageProducer), _actionManager);

        //and the Command updater
        CommandUpdater commandUpdater = new CommandUpdaterImpl(_actionManager);

        //start the Completion Info Consumer
         _messageConsumer = new BaseMessageConsumer(
               "JMS Completion Info Consumer",
                new DestinationData(JMSCompletionInfoListener.QUEUE_NAME,
                        DestinationType.QUEUE),
                new JMSCompletionInfoListener(commandUpdater)
        );

        try {
            _messageConsumer.startJms(provider);
        } catch (JMSException e) {
            LOG.log(Level.WARNING, "Problem starting message consumer", e);
        }


        //advertise the GMP service in the OSGi framework
        _registration = context.registerService(
                CommandSender.class.getName(),
                commandSender, null);

        return provider;
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {

        LOG.info("Stopping Command Sender service bundle");

        _actionMessageProducer.close();

        _messageConsumer.stopJms();
        //notify the OSGi framework this service is not longer available
        _registration.unregister();

        LOG.info("Removing JMS Service provider");
        context.ungetService(serviceReference);

        _actionMessageProducer = null;
    }



}

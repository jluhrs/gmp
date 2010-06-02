package edu.gemini.aspen.gmp.commands.osgi;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import java.util.logging.Logger;

import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.model.executors.SequenceCommandExecutorStrategy;
import edu.gemini.aspen.gmp.commands.impl.CommandSenderImpl;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.jms.SequenceCommandSenderReply;
import edu.gemini.aspen.gmp.commands.jms.CompletionInfoListener;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.osgi.JmsProviderTracker;

/**
 * The OSGi Activator for the Sequence Command Service
 */
public class Activator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(
            Activator.class.getName());

    private JmsProviderTracker _jmsTracker;

    private ActionManager _actionManager;

    private SequenceCommandSenderReply _sequenceCommandSenderReply;

    private BaseMessageConsumer _messageConsumer;

    private ServiceRegistration _registration;

    public Activator() {

        _actionManager = new ActionManager();

         //and the Command updater
        CommandUpdater commandUpdater = new CommandUpdaterImpl(_actionManager);

        //Creates the Completion Info Consumer
        _messageConsumer = new BaseMessageConsumer(
               "JMS Completion Info Consumer",
                new DestinationData(CompletionInfoListener.QUEUE_NAME,
                        DestinationType.QUEUE),
                new CompletionInfoListener(commandUpdater)
        );

        //and the Sequence Command Producer
        _sequenceCommandSenderReply = new
                SequenceCommandSenderReply("Sequence Command Producer");

    }

    public void start(BundleContext bundleContext) throws Exception {
        _actionManager.start();

        CommandSender commandSender = new CommandSenderImpl(_actionManager,
                _sequenceCommandSenderReply,
                new SequenceCommandExecutorStrategy(
                        new JmsActionMessageBuilder(),
                        _actionManager
                ));

        _jmsTracker = new JmsProviderTracker(bundleContext, "Sequence Command Sender/Completion Information Receiver");
        _jmsTracker.registerJmsArtifact(_messageConsumer);
        _jmsTracker.registerJmsArtifact(_sequenceCommandSenderReply);

        _jmsTracker.open();

        //advertise the Command Sender service in the OSGi framework
        _registration = bundleContext.registerService(
                CommandSender.class.getName(),
                commandSender, null);
        LOG.info("Sequence Command Sender Bundle started");

    }

    public void stop(BundleContext bundleContext) throws Exception {

        _jmsTracker.close();
        _jmsTracker = null;

        //notify the OSGi framework this service is not longer available
        _registration.unregister();

        _actionManager.stop();
        LOG.info("Sequence Command Sender Bundle stopped");

    }
}

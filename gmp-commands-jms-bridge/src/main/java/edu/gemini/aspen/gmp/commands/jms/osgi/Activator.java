package edu.gemini.aspen.gmp.commands.jms.osgi;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesBridge;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesBridgeImpl;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesConsumer;
import edu.gemini.aspen.gmp.commands.jms.instrumentbridge.ActionMessageActionSender;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.jms.api.JmsArtifact;
import edu.gemini.jms.api.JmsProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceTracker<CommandSender, CommandSender> csServiceTracker;
    private ServiceTracker<JmsProvider, JmsProvider> jmsProviderServiceTracker;
    private ServiceRegistration<CommandMessagesBridge> bridgeServiceRegistration;
    private ServiceRegistration<JmsArtifact> commandMessagesConsumerRegistration;
    private ServiceRegistration<?> amServiceRegistration;

    @Override
    public void start(final BundleContext context) throws Exception {
        ActionMessageActionSender actionMessageActionSender = new ActionMessageActionSender();
        amServiceRegistration = context.registerService(new String[]{ActionSender.class.getName(), JmsArtifact.class.getName()}, actionMessageActionSender, new Hashtable<String, Object>());

        csServiceTracker = new ServiceTracker<CommandSender, CommandSender>(context, CommandSender.class, new ServiceTrackerCustomizer<CommandSender, CommandSender>() {

            @Override
            public CommandSender addingService(ServiceReference<CommandSender> reference) {
                final CommandSender cs = context.getService(reference);
                jmsProviderServiceTracker = new ServiceTracker<JmsProvider, JmsProvider>(context, JmsProvider.class, new ServiceTrackerCustomizer<JmsProvider, JmsProvider>() {

                    @Override
                    public JmsProvider addingService(ServiceReference<JmsProvider> jmsProviderReference) {
                        JmsProvider jmsProvider = context.getService(jmsProviderReference);
                        if (bridgeServiceRegistration == null) {
                            CommandMessagesBridgeImpl bridge = new CommandMessagesBridgeImpl(jmsProvider, cs);
                            CommandMessagesConsumer commandMessagesConsumer = new CommandMessagesConsumer(bridge);
                            bridgeServiceRegistration = context.registerService(CommandMessagesBridge.class, bridge, new Hashtable<String, String>());
                            commandMessagesConsumerRegistration = context.registerService(JmsArtifact.class, commandMessagesConsumer, new Hashtable<String, String>());
                        }
                        return jmsProvider;
                    }

                    @Override
                    public void modifiedService(ServiceReference<JmsProvider> reference, JmsProvider JmsProvider) {

                    }

                    @Override
                    public void removedService(ServiceReference<JmsProvider> reference, JmsProvider JmsProvider) {
                        if (bridgeServiceRegistration != null) {
                            bridgeServiceRegistration.unregister();
                            bridgeServiceRegistration = null;
                        }
                        if (commandMessagesConsumerRegistration != null) {
                            commandMessagesConsumerRegistration.unregister();
                            commandMessagesConsumerRegistration = null;
                        }
                    }
                });
                jmsProviderServiceTracker.open();
                return context.getService(reference);
            }

            @Override
            public void modifiedService(ServiceReference<CommandSender> CommandSenderServiceReference, CommandSender CommandSender) {

            }

            @Override
            public void removedService(ServiceReference<CommandSender> reference, CommandSender CommandSender) {
                if (jmsProviderServiceTracker != null) {
                    jmsProviderServiceTracker.close();
                    jmsProviderServiceTracker = null;
                }
            }
        });
        csServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (jmsProviderServiceTracker != null) {
            jmsProviderServiceTracker.close();
            jmsProviderServiceTracker = null;
        }
        if (csServiceTracker != null) {
            csServiceTracker.close();
            csServiceTracker = null;
        }
        if (bridgeServiceRegistration != null) {
            bridgeServiceRegistration.unregister();
            bridgeServiceRegistration = null;
        }
        if (commandMessagesConsumerRegistration != null) {
            commandMessagesConsumerRegistration.unregister();
            commandMessagesConsumerRegistration = null;
        }
        if (amServiceRegistration != null) {
            amServiceRegistration.unregister();
            amServiceRegistration = null;
        }

    }
}

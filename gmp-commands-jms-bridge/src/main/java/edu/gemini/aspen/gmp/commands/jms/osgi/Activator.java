package edu.gemini.aspen.gmp.commands.jms.osgi;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CommandUpdater;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesBridge;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesBridgeImpl;
import edu.gemini.aspen.gmp.commands.jms.clientbridge.CommandMessagesConsumer;
import edu.gemini.aspen.gmp.commands.jms.instrumentbridge.ActionMessageActionSender;
import edu.gemini.aspen.gmp.commands.jms.instrumentbridge.CompletionInfoListener;
import edu.gemini.aspen.gmp.commands.jms.instrumentbridge.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
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
    private ServiceTracker<CommandUpdater, CommandUpdater> cuServiceTracker;
    private ServiceTracker<CommandSender, CommandSender> csServiceTracker;
    private ServiceTracker<JmsProvider, JmsProvider> jmsProviderServiceTracker;
    private ServiceRegistration<CommandMessagesBridge> bridgeServiceRegistration;
    private ServiceRegistration<JmsArtifact> commandMessagesConsumerRegistration;
    private ServiceRegistration<JmsArtifact> infoListenerRegistration;
    private ServiceRegistration<?> amServiceRegistration;
    private ServiceRegistration<ActionMessageBuilder> mbServiceRegistration;

    @Override
    public void start(final BundleContext context) {
        ActionMessageActionSender actionMessageActionSender = new ActionMessageActionSender();
        amServiceRegistration = context.registerService(new String[]{ActionSender.class.getName(), JmsArtifact.class.getName()}, actionMessageActionSender, new Hashtable<String, Object>());
        JmsActionMessageBuilder jmsActionMessageBuilder = new JmsActionMessageBuilder();
        mbServiceRegistration = context.registerService(ActionMessageBuilder.class, jmsActionMessageBuilder, new Hashtable<String, Object>());

        cuServiceTracker = new ServiceTracker<CommandUpdater, CommandUpdater>(context, CommandUpdater.class, new ServiceTrackerCustomizer<CommandUpdater, CommandUpdater>() {

            @Override
            public CommandUpdater addingService(ServiceReference<CommandUpdater> reference) {
                CommandUpdater commandUpdater = context.getService(reference);
                CompletionInfoListener infoListener = new CompletionInfoListener(commandUpdater);
                infoListenerRegistration = context.registerService(JmsArtifact.class, infoListener, new Hashtable<String, Object>());
                return commandUpdater;
            }

            @Override
            public void modifiedService(ServiceReference<CommandUpdater> commandUpdaterServiceReference, CommandUpdater commandUpdater) {

            }

            @Override
            public void removedService(ServiceReference<CommandUpdater> commandUpdaterServiceReference, CommandUpdater commandUpdater) {
                if (infoListenerRegistration != null) {
                    infoListenerRegistration.unregister();
                    infoListenerRegistration = null;
                }
            }
        });
        cuServiceTracker.open();

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
    public void stop(BundleContext context) {
        if (jmsProviderServiceTracker != null) {
            jmsProviderServiceTracker.close();
            jmsProviderServiceTracker = null;
        }
        if (csServiceTracker != null) {
            csServiceTracker.close();
            csServiceTracker = null;
        }
        if (cuServiceTracker != null) {
            cuServiceTracker.close();
            cuServiceTracker = null;
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
        if (mbServiceRegistration != null) {
            mbServiceRegistration.unregister();
            mbServiceRegistration = null;
        }
        if (infoListenerRegistration != null) {
            infoListenerRegistration.unregister();
            infoListenerRegistration = null;
        }

    }
}

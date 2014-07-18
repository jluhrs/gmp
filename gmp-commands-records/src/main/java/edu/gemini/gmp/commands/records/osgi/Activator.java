package edu.gemini.gmp.commands.records.osgi;

import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.gmp.commands.records.CommandRecordsBuilder;
import edu.gemini.gmp.top.Top;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Hashtable;

public class Activator implements BundleActivator {
    private ServiceTracker<ChannelAccessServer, ChannelAccessServer> caServiceTracker;
    private ServiceTracker<Top, Top> topServiceTracker;
    private ServiceTracker<CommandSender, CommandSender> csServiceTracker;
    private ServiceRegistration<ManagedServiceFactory> factoryService;

    @Override
    public void start(final BundleContext context) throws Exception {
        caServiceTracker = new ServiceTracker<ChannelAccessServer, ChannelAccessServer>(context, ChannelAccessServer.class, new ServiceTrackerCustomizer<ChannelAccessServer, ChannelAccessServer>() {

            @Override
            public ChannelAccessServer addingService(ServiceReference<ChannelAccessServer> reference) {
                final ChannelAccessServer cas = context.getService(reference);
                topServiceTracker = new ServiceTracker<Top, Top>(context, Top.class, new ServiceTrackerCustomizer<Top, Top>() {

                    @Override
                    public Top addingService(ServiceReference<Top> topReference) {
                        final Top top = context.getService(topReference);
                        csServiceTracker = new ServiceTracker<CommandSender, CommandSender>(context, CommandSender.class, new ServiceTrackerCustomizer<CommandSender, CommandSender>() {
                            private RecordsManagedFactory factory;

                            @Override
                            public CommandSender addingService(ServiceReference<CommandSender> csReference) {
                                CommandSender cs = context.getService(csReference);
                                factory = new RecordsManagedFactory(context, cas, cs, top);

                                Hashtable<String, String> props = new Hashtable<String, String>();
                                props.put("service.pid", CommandRecordsBuilder.class.getName());

                                factoryService = context.registerService(ManagedServiceFactory.class, factory, props);

                                return null;
                            }

                            @Override
                            public void modifiedService(ServiceReference<CommandSender> commandSenderServiceReference, CommandSender commandSender) {

                            }

                            @Override
                            public void removedService(ServiceReference<CommandSender> commandSenderServiceReference, CommandSender commandSender) {
                                if (factory != null) {
                                    factory.stopServices();
                                    factory = null;
                                }
                                if (factoryService != null) {
                                    factoryService.unregister();
                                    factoryService = null;
                                }
                            }
                        });
                        csServiceTracker.open();
                        return top;
                    }

                    @Override
                    public void modifiedService(ServiceReference<Top> reference, Top top) {

                    }

                    @Override
                    public void removedService(ServiceReference<Top> reference, Top top) {
                        if (csServiceTracker != null) {
                            csServiceTracker.close();
                            csServiceTracker = null;
                        }
                    }
                });
                topServiceTracker.open();
                return context.getService(reference);
            }

            @Override
            public void modifiedService(ServiceReference<ChannelAccessServer> channelAccessServerServiceReference, ChannelAccessServer channelAccessServer) {

            }

            @Override
            public void removedService(ServiceReference<ChannelAccessServer> reference, ChannelAccessServer channelAccessServer) {
                if (topServiceTracker != null) {
                    topServiceTracker.close();
                    topServiceTracker = null;
                }
            }
        });
        caServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (csServiceTracker != null) {
            csServiceTracker.close();
            csServiceTracker = null;
        }
        if (caServiceTracker != null) {
            caServiceTracker.close();
            caServiceTracker = null;
        }
        if (topServiceTracker != null) {
            topServiceTracker.close();
            topServiceTracker = null;
        }
        if (factoryService != null) {
            factoryService.unregister();
            factoryService = null;
        }
    }
}

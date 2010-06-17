package edu.gemini.aspen.gmp.statusgw.osgi;

import edu.gemini.aspen.gmp.statusgw.StatusDatabaseServiceDecorator;
import edu.gemini.aspen.gmp.statusgw.jms.JmsStatusItemDispatcher;
import edu.gemini.aspen.gmp.statusgw.jms.StatusRequestListener;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the Status Gateway bundle
 */
public class Activator implements BundleActivator {

//    private Supervisor _supervisor;

    private StatusDatabaseTracker _dbTracker;
    private JmsProviderTracker _jmsTracker;

    public void start(BundleContext bundleContext) throws Exception {

        StatusDatabaseServiceDecorator decorator = new StatusDatabaseServiceDecorator();

        JmsStatusItemDispatcher dispatcher = new JmsStatusItemDispatcher("Gateway Status Dispatcher");

        //Create the message consumer for status items requests
        BaseMessageConsumer consumer = new BaseMessageConsumer(
               "Gateway Status Consumer",
                new DestinationData(StatusRequestListener.TOPIC_NAME,
                        DestinationType.TOPIC),
                new StatusRequestListener(decorator, dispatcher)
        );


        _jmsTracker = new JmsProviderTracker(bundleContext,
                consumer, dispatcher);
        _jmsTracker.open();

        _dbTracker = new StatusDatabaseTracker(bundleContext, decorator);
        _dbTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {


        _dbTracker.close();
        _dbTracker = null;

        _jmsTracker.close();
        _jmsTracker = null;
    }
}
package edu.gemini.aspen.gmp.statusgw.osgi;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.statusgw.StatusDatabaseServiceDecorator;
import edu.gemini.aspen.gmp.statusgw.jms.JmsStatusDispatcher;
import edu.gemini.aspen.gmp.statusgw.jms.StatusItemRequestListener;
import edu.gemini.aspen.gmp.statusgw.jms.StatusNamesRequestListener;
import edu.gemini.aspen.gmp.statusgw.jms.StatusTopics;
import edu.gemini.jms.api.BaseMessageConsumer;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import edu.gemini.jms.api.JmsSimpleMessageSelector;
import edu.gemini.jms.api.osgi.JmsProviderTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the Status Gateway bundle
 */
public class Activator implements BundleActivator {

//    private Supervisor _supervisor;

    private StatusDatabaseTracker _dbTracker;
    private JmsProviderTracker _jmsStatusTracker;

    public void start(BundleContext bundleContext) throws Exception {

        StatusDatabaseServiceDecorator decorator = new StatusDatabaseServiceDecorator();

        JmsStatusDispatcher dispatcher = new JmsStatusDispatcher("Gateway Status Dispatcher");

        //Create the message consumer for status items requests
        BaseMessageConsumer consumer = new BaseMessageConsumer(
               "Gateway Status Consumer",
                new DestinationData(StatusTopics.TOPIC_NAME,
                        DestinationType.TOPIC),
                new StatusItemRequestListener(decorator, dispatcher),
                new JmsSimpleMessageSelector(JmsKeys.GW_STATUS_REQUEST_TYPE_PROPERTY+ " = '" +JmsKeys.GW_STATUS_REQUEST_TYPE_ITEM +"'")
        );


        //Create the message consumer for status names requests
        BaseMessageConsumer namesConsumer = new BaseMessageConsumer(
               "Gateway Status Names Consumer",
                new DestinationData(StatusTopics.TOPIC_NAME,
                        DestinationType.TOPIC),
                new StatusNamesRequestListener(decorator, dispatcher),
                new JmsSimpleMessageSelector(JmsKeys.GW_STATUS_REQUEST_TYPE_PROPERTY+ " = '" +JmsKeys.GW_STATUS_REQUEST_TYPE_NAMES +"'")
        );


        _jmsStatusTracker = new JmsProviderTracker(bundleContext,
                consumer, namesConsumer, dispatcher);
        _jmsStatusTracker.open();



        _dbTracker = new StatusDatabaseTracker(bundleContext, decorator);
        _dbTracker.open();
    }

    public void stop(BundleContext bundleContext) throws Exception {


        _dbTracker.close();
        _dbTracker = null;
        
        _jmsStatusTracker.close();
        _jmsStatusTracker = null;
    }
}
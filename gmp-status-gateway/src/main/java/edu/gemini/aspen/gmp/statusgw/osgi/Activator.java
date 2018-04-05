package edu.gemini.aspen.gmp.statusgw.osgi;

import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.aspen.gmp.statusgw.StatusDatabaseServiceDecorator;
import edu.gemini.aspen.gmp.statusgw.jms.JmsStatusDispatcher;
import edu.gemini.aspen.gmp.statusgw.jms.MultipleStatusItemsRequestListener;
import edu.gemini.aspen.gmp.statusgw.jms.StatusItemRequestListener;
import edu.gemini.aspen.gmp.statusgw.jms.StatusNamesRequestListener;
import edu.gemini.jms.api.*;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * Activator class for the Status Gateway bundle
 */
public class Activator implements BundleActivator {

    private StatusDatabaseTracker _dbTracker;

    public void start(BundleContext bundleContext) {

        StatusDatabaseServiceDecorator decorator = new StatusDatabaseServiceDecorator();

        JmsStatusDispatcher dispatcher = new JmsStatusDispatcher("Gateway Status Dispatcher");

        //Create the message consumer for status items requests
        BaseMessageConsumer consumer = new BaseMessageConsumer(
                "Gateway Status Consumer",
                new DestinationData(JmsKeys.GW_STATUS_REQUEST_DESTINATION,
                        DestinationType.TOPIC),
                new StatusItemRequestListener(decorator, dispatcher),
                new JmsSimpleMessageSelector(JmsKeys.GW_STATUS_REQUEST_TYPE_PROPERTY + " = '" + JmsKeys.GW_STATUS_REQUEST_TYPE_ITEM + "'")
        );


        //Create the message consumer for status names requests
        BaseMessageConsumer namesConsumer = new BaseMessageConsumer(
                "Gateway Status Names Consumer",
                new DestinationData(JmsKeys.GW_STATUS_REQUEST_DESTINATION,
                        DestinationType.TOPIC),
                new StatusNamesRequestListener(decorator, dispatcher),
                new JmsSimpleMessageSelector(JmsKeys.GW_STATUS_REQUEST_TYPE_PROPERTY + " = '" + JmsKeys.GW_STATUS_REQUEST_TYPE_NAMES + "'")
        );

        //Create the message consumer for multiple status items requests
        BaseMessageConsumer multipleStatusItemsConsumer = new BaseMessageConsumer(
                "Gateway Multiple StatusItems Consumer",
                new DestinationData(JmsKeys.GW_STATUS_REQUEST_DESTINATION,
                        DestinationType.TOPIC),
                new MultipleStatusItemsRequestListener(decorator, dispatcher),
                new JmsSimpleMessageSelector(JmsKeys.GW_STATUS_REQUEST_TYPE_PROPERTY + " = '" + JmsKeys.GW_STATUS_REQUEST_TYPE_ALL + "'")
        );

        bundleContext.registerService(JmsArtifact.class.getName(), dispatcher, null);
        bundleContext.registerService(JmsArtifact.class.getName(), consumer, null);
        bundleContext.registerService(JmsArtifact.class.getName(), namesConsumer, null);
        bundleContext.registerService(JmsArtifact.class.getName(), multipleStatusItemsConsumer, null);


        _dbTracker = new StatusDatabaseTracker(bundleContext, decorator);
        _dbTracker.open();
    }

    public void stop(BundleContext bundleContext) {
        _dbTracker.close();
        _dbTracker = null;
    }
}
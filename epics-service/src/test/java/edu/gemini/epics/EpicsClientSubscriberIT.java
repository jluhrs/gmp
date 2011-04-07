package edu.gemini.epics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Basic integration test for EpicsService \verifying that the services can run and its properties be set
 */
@RunWith(JUnit4TestRunner.class)
public class EpicsClientSubscriberIT extends EpicsServiceBaseIntegration {
    @Test
    public void registerMockEpicsClient() throws InterruptedException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<String, String[]>();
        serviceProperties.put(EpicsClient.EPICS_CHANNELS, new String[]{"ws:wsFilter.VALL"});
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Give it 3 seconds
        TimeUnit.MILLISECONDS.sleep(2000);
        assertTrue(epicsClient.wasConnectedCalled());
        assertFalse(epicsClient.wasDisconnectedCalled());
        assertTrue(epicsClient.getUpdatesCount() > 0);

        serviceRegistration.unregister();
        assertTrue(epicsClient.wasDisconnectedCalled());
    }

    @Test
    public void registerMockEpicsClientWithNoProperties() throws InterruptedException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<String, String[]>();
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Wait a little bit
        TimeUnit.MILLISECONDS.sleep(500);
        assertFalse(epicsClient.wasConnectedCalled());

        serviceRegistration.unregister();
    }

    @Test
    public void testDisconnectUponObserverUnRegistration() throws InterruptedException, BundleException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<String, String[]>();
        serviceProperties.put(EpicsClient.EPICS_CHANNELS, new String[]{"ws:wsFilter.VALL"});
        context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Wait a little bit
        TimeUnit.MILLISECONDS.sleep(500);

        getEpicsServiceBundle().stop();

        assertTrue(epicsClient.wasDisconnectedCalled());
    }

}

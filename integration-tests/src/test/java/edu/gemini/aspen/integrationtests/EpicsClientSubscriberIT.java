package edu.gemini.aspen.integrationtests;

import edu.gemini.epics.api.EpicsClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Basic integration test for EpicsService verifying that the services can run and its properties be set
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EpicsClientSubscriberIT extends EpicsServiceBaseIntegration {

    @Test
    public void registerMockEpicsClientWithNoProperties() throws InterruptedException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<>();
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Wait a little bit
        TimeUnit.MILLISECONDS.sleep(500);
        assertFalse(epicsClient.wasConnectedCalled());

        serviceRegistration.unregister();
    }

    @Test
    public void testDisconnectUponObserverUnRegistration() throws InterruptedException, BundleException {
        EpicsClientMock epicsClient = new EpicsClientMock();

        Dictionary<String, String[]> serviceProperties = new Hashtable<>();
        serviceProperties.put(EpicsClient.EPICS_CHANNELS, new String[]{"ws:wsFilter.VALL"});
        ServiceRegistration serviceRegistration = context.registerService(EpicsClient.class.getName(), epicsClient, serviceProperties);

        // Wait a little bit
        TimeUnit.MILLISECONDS.sleep(500);

        getEpicsServiceBundle().stop();

        assertTrue(epicsClient.wasDisconnectedCalled());
        serviceRegistration.unregister();
    }

}

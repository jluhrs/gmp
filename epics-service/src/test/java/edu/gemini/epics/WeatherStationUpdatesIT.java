package edu.gemini.epics;

import com.cosylab.epics.caj.CAJContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import edu.gemini.epics.api.EpicsClient;
import edu.gemini.epics.impl.ChannelBindingSupport;
import edu.gemini.epics.impl.EpicsObserverImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

/**
 * Integration test that verifies that we get updates from the Weather Station
 */
@Ignore
public class WeatherStationUpdatesIT {
    /**
     * Map the channel names to friendly text names.
     */
    private static final Map<String, String> CHANNELS = ImmutableMap.of(
            "ws:wsFilter.VALP", "Humidity",
            "ws:wsFilter.VALO", "Pressure",
            "ws:wsFilter.VALL", "Temperature",
            "ws:wsFilter.VALN", "Wind Direction",
            "ws:wsFilter.VALM", "Wind Speed");

    private ChannelBindingSupport cbs;
    private CAJContext context;

    @Before
    public void setUp() throws Exception {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.17.2.255");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        context = (CAJContext) JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
    }

    @After
    public void cleanUp() throws CAException {
        context.destroy();
    }

    @Test
    public void testWeatherReads() throws InterruptedException, CAException {
        WeatherStationEpicsClient weatherStationEpicsClient = new WeatherStationEpicsClient();

        EpicsService epicsService = new EpicsService(context);
        EpicsObserver observer = new EpicsObserverImpl(epicsService);

        observer.registerEpicsClient(weatherStationEpicsClient, CHANNELS.keySet());

        // Give it 5 seconds
        TimeUnit.MILLISECONDS.sleep(5000);

        assertTrue(weatherStationEpicsClient.gotUpdates());

        cbs.close();
    }

    private class WeatherStationEpicsClient implements EpicsClient {
        private List<Double> results = Lists.newArrayList();

        public <T> void valueChanged(String channel, List<T> values) {
            if (values != null && !values.isEmpty() && values.get(0) instanceof Double) {
                List<Double> doubleValues = (List<Double>) values;
                System.out.println(CHANNELS.get(channel) + ": " + doubleValues);
                results.addAll(doubleValues);
            }
        }

        public void connected() {
            // This will not be called
        }

        public void disconnected() {
            // This will not be called
        }

        public boolean gotUpdates() {
            return !results.isEmpty();
        }
    }
}

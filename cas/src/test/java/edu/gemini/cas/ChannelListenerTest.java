package edu.gemini.cas;

import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ChannelAlarmListener;
import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Class ChannelListenerTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/16/11
 */
public class ChannelListenerTest {
    private class TestChannelListener extends CountDownLatch implements ChannelListener<Double> {
        final Logger LOG = Logger.getLogger(ChannelListenerTest.class.getName());

        public TestChannelListener(int latchCount) {
            super(latchCount);
        }

        @Override
        public void valueChanged(String channelName, List<Double> values) {
            LOG.info("Received: " + values.get(0));
            countDown();
        }
    }

    private class TestChannelAlarmListener extends CountDownLatch implements ChannelAlarmListener<Double> {
        final Logger LOG = Logger.getLogger(ChannelListenerTest.class.getName());

        public TestChannelAlarmListener(int latchCount) {
            super(latchCount);
        }

        @Override
        public void valueChanged(String channelName, List<Double> values, Status status, Severity severity) {
            LOG.info("Received: " + values.get(0) + ", status: " + status + ", severity: " + severity);
            countDown();
        }
    }

    @Test
    public void test() throws CAException, InterruptedException, TimeoutException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch = cas.createChannel("test", 1.0);
        TestChannelListener chListener = new TestChannelListener(1);
        ch.registerListener(chListener);
        ch.setValue(2.0);
        assertTrue(chListener.await(1, TimeUnit.SECONDS));
        assertEquals(new Double(2.0), new Double(((double[]) ch.getDBR().getValue())[0]));
        cas.stop();

    }

    @Test
    public void testAlarm() throws CAException, InterruptedException, TimeoutException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        AlarmChannel<Double> ch = cas.createAlarmChannel("test", 1.0);
        TestChannelAlarmListener chListener = new TestChannelAlarmListener(1);
        ch.registerListener(chListener);

        ch.setAlarm(Status.HIGH_ALARM, Severity.MAJOR_ALARM, "test alarm");

        assertTrue(chListener.await(1, TimeUnit.SECONDS));
        assertEquals(new Double(1.0), new Double(((double[]) ch.getDBR().getValue())[0]));
        assertEquals(Status.HIGH_ALARM, ((STS) ch.getDBR()).getStatus());
        assertEquals(Severity.MAJOR_ALARM, ((STS) ch.getDBR()).getSeverity());
        cas.stop();

    }

    @Test
    public void test2listeners() throws CAException, InterruptedException, TimeoutException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch = cas.createChannel("test", 1.0);
        TestChannelListener chListener = new TestChannelListener(1);
        ch.registerListener(chListener);
        TestChannelListener chListener2 = new TestChannelListener(1);
        ch.registerListener(chListener2);
        ch.setValue(2.0);
        assertTrue(chListener.await(1, TimeUnit.SECONDS));
        assertTrue(chListener2.await(1, TimeUnit.SECONDS));
        assertEquals(new Double(2.0), new Double(((double[]) ch.getDBR().getValue())[0]));
        cas.stop();

    }

    @Test
    public void testUnregisterListener() throws CAException, InterruptedException, TimeoutException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch = cas.createChannel("test", 1.0);

        TestChannelListener chListener = new TestChannelListener(1);
        ch.registerListener(chListener);
        ch.unRegisterListener(chListener);

        TestChannelListener chListener2 = new TestChannelListener(1);
        ch.registerListener(chListener2);
        ch.setValue(2.0);
        assertTrue(chListener2.await(1, TimeUnit.SECONDS));
        assertFalse(chListener.await(1, TimeUnit.SECONDS));

        assertEquals(new Double(2.0), new Double(((double[]) ch.getDBR().getValue())[0]));
        cas.stop();

    }
}

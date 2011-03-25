package edu.gemini.cas;

import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.dbr.DBR;
import org.junit.Test;

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
    private class TestChannelListener extends CountDownLatch implements ChannelListener {
            final Logger LOG = Logger.getLogger(ChannelListenerTest.class.getName());

        public TestChannelListener(int latchCount) {
            super(latchCount);
        }

        @Override
        public void valueChange(DBR dbr) {
            LOG.info("Received: " + ((double[]) dbr.getValue())[0]);
            countDown();
        }
    }

    @Test
    public  void test() throws CAException, InterruptedException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch=cas.createChannel("test", 1.0);
        TestChannelListener chListener = new TestChannelListener(1);
        ch.registerListener(chListener);
        ch.setValue(2.0);
        assertTrue(chListener.await(1, TimeUnit.SECONDS));
        assertEquals(new Double(2.0), new Double(((double[]) ch.getDBR().getValue())[0]));
        cas.stop();

    }

    @Test
    public  void test2listeners() throws CAException, InterruptedException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch=cas.createChannel("test", 1.0);
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
    public  void testUnregisterListener() throws CAException, InterruptedException {

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Double> ch=cas.createChannel("test", 1.0);

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

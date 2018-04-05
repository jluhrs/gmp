package edu.gemini.aspen.integrationtests;

import edu.gemini.cas.AlarmChannel;
import edu.gemini.epics.*;
import edu.gemini.epics.api.ChannelAlarmListener;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.impl.EpicsReaderImpl;
import edu.gemini.epics.impl.EpicsWriterImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChannelListenerTest extends NewEpicsTestBase {
    private EpicsReader epicsReader;
    private EpicsWriter epicsWriter;

    @Before
    public void setup() throws CAException {
        super.setup();
        epicsReader = new EpicsReaderImpl(new EpicsService(context));
        epicsWriter = new EpicsWriterImpl(new EpicsService(context));
    }


    class MyListener implements ChannelListener<Double> {
        private Double last;

        @Override
        public void valueChanged(String channelName, List<Double> values) {
            last = values.get(0);
        }

        public Double getLast() {
            return last;
        }
    }


    private class MyAlarmListener extends CountDownLatch implements ChannelAlarmListener<Double> {
        final Logger LOG = Logger.getLogger(ChannelListenerTest.class.getName());
        private Double last;
        private Status status;
        private Severity severity;

        public MyAlarmListener(int latchCount) {
            super(latchCount);
        }

        @Override
        public void valueChanged(String channelName, List<Double> values, Status status, Severity severity) {
            LOG.info("Received: " + values.get(0) + ", status: " + status + ", severity: " + severity);
            this.last = values.get(0);
            this.status = status;
            this.severity = severity;

            countDown();
        }
        public Double getValue() {
            return last;
        }
        public Status getStatus() {
            return status;
        }
        public Severity getSeverity() {
            return severity;
        }
    }

    @Test
    public void testListener() throws CAException, InterruptedException, TimeoutException {
        ReadOnlyClientEpicsChannel<Double> readCh = epicsReader.getDoubleChannel(doubleName);
        assertTrue(readCh.isValid());
        assertEquals((Double) 1.0, readCh.getFirst());

        ReadWriteClientEpicsChannel<Double> writeCh = epicsWriter.getDoubleChannel(doubleName);
        assertTrue(writeCh.isValid());

        MyListener listener = new MyListener();
        readCh.registerListener(listener);
        writeCh.setValue(2.0);
        Thread.sleep(100);
        assertEquals(new Double(2.0), listener.getLast());
        readCh.destroy();
        writeCh.destroy();
    }

    //todo: this test should work.
    @Ignore
    @Test
    public void testAlarmListener() throws CAException, InterruptedException, TimeoutException {
        AlarmChannel<Double>  alarmChannel = cas.createAlarmChannel("giapitest:doublealarm", 1.0);
        Thread.sleep(200);


        ReadOnlyClientEpicsChannel<Double> readCh = epicsReader.getDoubleChannel("giapitest:doublealarm");
        assertTrue(readCh.isValid());
        assertEquals((Double) 1.0, readCh.getFirst());


        MyAlarmListener listener = new MyAlarmListener(1);
        readCh.registerListener(listener);

        alarmChannel.setAlarm(Status.HIGH_ALARM, Severity.MAJOR_ALARM, "test alarm");
        assertEquals((Double) 1.0, readCh.getFirst());
        assertTrue(listener.await(1, TimeUnit.SECONDS));

        assertEquals(new Double(1.0), listener.getValue());
        assertEquals(Status.HIGH_ALARM, listener.getStatus());
        assertEquals(Severity.MAJOR_ALARM, listener.getSeverity());
        readCh.destroy();
        cas.destroyChannel(alarmChannel);
    }

    @Test
    public void testListenerForAsynChannel() throws CAException, InterruptedException, TimeoutException {
        ReadOnlyClientEpicsChannel readCh = epicsReader.getChannelAsync(doubleName);
        Thread.sleep(100);
        assertTrue(readCh.isValid());
        assertEquals((Double) 1.0, readCh.getFirst());

        ReadWriteClientEpicsChannel writeCh = epicsWriter.getChannelAsync(doubleName);
        Thread.sleep(100);
        assertTrue(writeCh.isValid());

        MyListener listener = new MyListener();
        readCh.registerListener(listener);
        writeCh.setValue(2.0);
        Thread.sleep(100);
        assertEquals(new Double(2.0), listener.getLast());
        readCh.destroy();
        writeCh.destroy();
    }

}

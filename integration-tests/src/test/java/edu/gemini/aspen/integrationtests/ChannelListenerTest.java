package edu.gemini.aspen.integrationtests;

import edu.gemini.epics.*;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.epics.impl.NewEpicsReaderImpl;
import edu.gemini.epics.impl.NewEpicsWriterImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//import java.lang.ref.Reference;
//import java.lang.ref.WeakReference;

public class ChannelListenerTest extends NewEpicsTestBase {
    private NewEpicsReader epicsReader;
    private NewEpicsWriter epicsWriter;

    @Before
    public void setup() throws CAException {
        super.setup();
        epicsReader = new NewEpicsReaderImpl(new EpicsService(context));
        epicsWriter = new NewEpicsWriterImpl(new EpicsService(context));
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

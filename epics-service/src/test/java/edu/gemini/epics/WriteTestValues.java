package edu.gemini.epics;

import edu.gemini.epics.impl.EpicsWriterImpl;
import gov.aps.jca.*;

import java.util.*;

/**
 * Trivial test to write a value in an Epics Channel
 */
public class WriteTestValues {
    /**
     * Map the channel names to friendly text names.
     */
    private static final Map<String, String> CHANNELS = new TreeMap<String, String>();

    static {
        CHANNELS.put("tst:array.J", "Array 10 Elements");
    }

    private EpicsWriter _writer;

    public WriteTestValues() throws CAException, EpicsException {
        Context context = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        _writer = new EpicsWriterImpl(new EpicsService(context, "172.16.2.24"));
        for (String s : CHANNELS.keySet()) {
            _writer.bindChannel(s);
        }
    }

    public void writeValue(String channel, Double[] value) {
        try {
            _writer.write(channel, value);
        } catch (EpicsException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        ((EpicsWriterImpl) _writer).close();
        super.finalize();
    }

    public static void main(String[] args) throws EpicsException, CAException, InterruptedException {
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        Double values[] = {1.1, 2.2, 3.3, 4.4, 5.5, 6.6, 7.7, 8.8, 9.9, 10.10};

        List<Double> l = Arrays.asList(values);

        WriteTestValues test = new WriteTestValues();

        for (int i = 0; i < 10; i++) {
            test.writeValue("tst:array.J", (Double[]) l.toArray());
            Collections.rotate(l, 1);
            Thread.sleep(50); // 20Hz updates
        }
        Thread.sleep(Long.MAX_VALUE);
    }
}

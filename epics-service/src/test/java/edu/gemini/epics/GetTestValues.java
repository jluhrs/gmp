package edu.gemini.epics;

import com.google.common.collect.Maps;
import edu.gemini.epics.impl.EpicsReaderImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;

import java.util.Map;

/**
 * An example to test the usage of the EpicsReader class.
 */
public class GetTestValues {
    private EpicsReader _reader;

    /**
     * Map the channel names to friendly text names.
     */
    private static final Map<String, String> CHANNELS = Maps.newHashMap();

    static {
        CHANNELS.put("tc1:sad:astCtx", "TCS Context");
    }

    public GetTestValues() throws CAException, EpicsException {
        Context context = JCALibrary.getInstance().createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        _reader = new EpicsReaderImpl(new EpicsService(context, "172.16.2.22"));
        for (String s : CHANNELS.keySet()) {
            _reader.bindChannel(s);
        }
    }

    Object getValues(String channel) throws EpicsException {
        return _reader.getValue(channel);
    }


    public static void main(String[] args) throws CAException, EpicsException {
        GetTestValues test = new GetTestValues();

        double[] values = (double[]) test.getValues("tc1:sad:astCtx");

        System.out.print("Value (" + values.length + "): ");
        for (double d : values) {
            System.out.print(d + " ");
        }
        System.out.println("");
    }
}

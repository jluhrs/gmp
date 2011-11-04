package edu.gemini.epics;

import com.google.common.collect.Maps;
import edu.gemini.epics.api.ReadOnlyChannel;
import gov.aps.jca.CAException;

import java.util.Map;

/**
 * An example to test the usage of the EpicsReader class.
 */
public class NewGetTestValues {
    private NewEpicsReader _reader;

    /**
     * Map the channel names to friendly text names.
     */
    private static final Map<String, String> CHANNELS = Maps.newHashMap();

    static {
        CHANNELS.put("tc1:sad:astCtx", "TCS Context");
    }

    public NewGetTestValues() throws CAException, EpicsException {
        EpicsService epicsService = new EpicsService("172.16.2.24");
//        epicsService.startService();
//        _reader = new NewEpicsReaderImpl(epicsService);
//        for (String s : CHANNELS.keySet()) {
//            _reader.bindChannel(s);
//        }
    }


    public static void main(String[] args) throws CAException, EpicsException {
        NewEpicsReader reader = null;

        ReadOnlyChannel<Double> channel = reader.getChannel("tc1:sad:astCtx");

        // System.out.println(channel.getArraySize());

    }
}

package edu.gemini.epics.test;

import edu.gemini.epics.impl.EpicsReader;
import edu.gemini.epics.EpicsException;
import edu.gemini.epics.IEpicsReader;
import gov.aps.jca.CAException;

import java.util.Map;
import java.util.TreeMap;

/**
 * An example to test the usage of the EpicsReader class.
 */
public class GetTest {


    private IEpicsReader _reader;

    /** Map the channel names to friendly text names. */
      private static final Map<String, String> CHANNELS = new TreeMap<String, String>();

      static {
          CHANNELS.put("tc1:sad:astCtx", "TCS Context");
      }



    public GetTest() throws CAException, EpicsException {
        _reader = new EpicsReader();
        for (String s : CHANNELS.keySet()) {
            _reader.bindChannel(s);
        }
    }

    Object getValues(String channel) throws EpicsException {
        return _reader.getValue(channel);
    }




    public static void main(String[] args) throws CAException, EpicsException {

        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "172.16.2.22");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");

        GetTest test = new GetTest();


        double[] values = (double[])test.getValues("tc1:sad:astCtx");

        System.out.print("Value (" + values.length + "): ");
        for (double d: values) {
            System.out.print(d + " ");
        }
        System.out.println("");

    

    }
}

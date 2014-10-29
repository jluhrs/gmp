package edu.gemini.gmp.commands.records;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.gmp.top.Top;
import edu.gemini.gmp.top.TopImpl;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.EpicsWriter;
import edu.gemini.epics.ReadWriteClientEpicsChannel;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.impl.EpicsWriterImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class ApplyTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/22/11
 */
public class ApplyTest {
    private static final Logger LOG = Logger.getLogger(ApplyTest.class.getName());
    private static final String xmlStr;

    private static File xmlFile = null;

    private ChannelAccessServerImpl cas;
    private final Top epicsTop = new TopImpl("gpitest","gpitest");
    private final String cadName = "observe";
    private CommandSender cs = MockFactory.createCommandSenderMock(epicsTop, cadName);
    private EpicsWriter epicsWriter;
    private CAJContext context;

    static {
        BufferedReader in = new BufferedReader(new InputStreamReader(ApplyTest.class.getResourceAsStream("giapi-apply-config.xml")));
        String xml = "";
        try {

            String line = in.readLine();
            while (line != null) {
                xml += line;
                line = in.readLine();

            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        xmlStr = xml;

        try {
            xmlFile = File.createTempFile("ApplyTest", ".xml");

            FileWriter xmlWrt = new FileWriter(xmlFile);

            xmlWrt.write(xmlStr);
            xmlWrt.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");
    }


    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();


        JCALibrary jca = JCALibrary.getInstance();
        context = (CAJContext) jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        epicsWriter = new EpicsWriterImpl(new EpicsService(context));

    }

    @After
    public void tearDown() throws CAException {
        cas.stop();
        context.destroy();

    }

    @Test
    public void applyTestObserve() throws CAException, InterruptedException, IOException, TimeoutException {
        CommandRecordsBuilder rf = new CommandRecordsBuilder(cas, cs, epicsTop, xmlFile.getPath());
        rf.start();

        Channel<Dir> dir = cas.createChannel(epicsTop.buildEpicsChannelName("apply.DIR"), Dir.CLEAR);
        Channel<Integer> val = cas.createChannel(epicsTop.buildEpicsChannelName("apply.VAL"), 0);
        Channel<Integer> clid = cas.createChannel(epicsTop.buildEpicsChannelName("apply.CLID"), 0);

        Channel<Integer> cadVal = cas.createChannel(epicsTop.buildEpicsChannelName(cadName + ".VAL"), 0);
        Channel<Integer> cadClid = cas.createChannel(epicsTop.buildEpicsChannelName(cadName + ".ICID"), 0);
        Channel<Integer> carClid = cas.createChannel(epicsTop.buildEpicsChannelName(cadName + "C.CLID"), 0);
        ReadWriteClientEpicsChannel<String> data_label = epicsWriter.getStringChannel(epicsTop.buildEpicsChannelName(cadName + ".DATA_LABEL"));

        data_label.setValue("label");
        Thread.sleep(1500);
        dir.setValue(Dir.START);
        Thread.sleep(1500);
        assertEquals(new Integer(1), clid.getFirst());
        assertEquals(new Integer(1), cadClid.getFirst());
        assertEquals(new Integer(1), carClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(1), val.getFirst());

        data_label.destroy();
        rf.stop();
    }

    @Test
    public void testReset() throws CAException, TimeoutException, InterruptedException {
        CommandRecordsBuilder rf = new CommandRecordsBuilder(cas, cs, epicsTop, xmlFile.getPath());
        rf.start();

        Channel<Reset> reset = cas.createChannel(epicsTop.buildEpicsChannelName("gmp:resetRecords"), Reset.NO_RESET);

        ReadWriteClientEpicsChannel<String> useAo = epicsWriter.getStringChannel(epicsTop.buildEpicsChannelName("configAo.useAo"));
        assertEquals("", useAo.getFirst());
        useAo.setValue("bla");
        assertEquals("bla", useAo.getFirst());
        useAo.destroy();
        reset.setValue(Reset.RESET);

        useAo = epicsWriter.getStringChannel(epicsTop.buildEpicsChannelName("configAo.useAo"));
        assertTrue(useAo.isValid());
        assertEquals("", useAo.getFirst());

        useAo.destroy();
        rf.stop();


    }


}

package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.aspen.gmp.epics.top.EpicsTopImpl;
import edu.gemini.epics.api.Channel;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Class ApplyTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/22/11
 */
public class ApplyTest {
    private static final Logger LOG = Logger.getLogger(ApplyTest.class.getName());
    private static final String xmlStr;

    private static final String xsdStr;
    private static File xsdFile = null;
    private static File xmlFile = null;

    private ChannelAccessServerImpl cas;
    private final EpicsTop epicsTop = new EpicsTopImpl("gpi");
    private final String cadName = "observe";
    private CommandSender cs = MockFactory.createCommandSenderMock(epicsTop, cadName);


    static {
        BufferedReader in = new BufferedReader(new InputStreamReader(ApplyTest.class.getResourceAsStream("../../../../../../giapi-apply-config.xml")));
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

        in = new BufferedReader(new InputStreamReader(ApplyTest.class.getResourceAsStream("../../../../../../giapi-apply-config.xsd")));
        String xsd = "";
        try {
            String line = in.readLine();
            while (line != null) {
                xsd += line;
                line = in.readLine();

            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        xsdStr = xsd;

        try {
            xmlFile = File.createTempFile("ApplyTest", ".xml");


            xsdFile = File.createTempFile("ApplyTest", ".xsd");

            FileWriter xmlWrt = new FileWriter(xmlFile);
            FileWriter xsdWrt = new FileWriter(xsdFile);

            xmlWrt.write(xmlStr);
            xsdWrt.write(xsdStr);
            xmlWrt.close();
            xsdWrt.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }


    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();
    }

    @After
    public void tearDown() throws CAException {
        cas.stop();
    }


    @Test
    public void applyTest() throws CAException, InterruptedException, IOException {
        ApplyRecord apply = new ApplyRecord(cas, cs, epicsTop, xmlFile.getPath(), xsdFile.getPath());
        apply.start();
        Channel<Dir> dir = cas.createChannel(epicsTop.buildChannelName("apply.DIR"), Dir.CLEAR);
        Channel<Integer> val = cas.createChannel(epicsTop.buildChannelName("apply.VAL"), 0);
        Channel<Integer> cadVal = cas.createChannel(epicsTop.buildChannelName(cadName + ".VAL"), 0);
        Channel<Integer> clid = cas.createChannel(epicsTop.buildChannelName("apply.CLID"), 0);
        Channel<Integer> cadClid = cas.createChannel(epicsTop.buildChannelName(cadName + ".ICID"), 0);
        Channel<String> data_label = cas.createChannel(epicsTop.buildChannelName(cadName + ".DATA_LABEL"), "");


        data_label.setValue("");
        dir.setValue(Dir.START);
        assertEquals(new Integer(1), clid.getFirst());
        assertEquals(new Integer(1), cadClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(1), val.getFirst());

        //special record apply/config.
        Channel<String> useAo = cas.createChannel(epicsTop.buildChannelName("configAo:useAo"), "");
        cadClid = cas.createChannel(epicsTop.buildChannelName("config.ICID"), 0);
        cadVal = cas.createChannel(epicsTop.buildChannelName("config.VAL"), 0);


        useAo.setValue("1");
        dir.setValue(Dir.START);
        assertEquals(new Integer(2), cadClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(2), val.getFirst());

        apply.stop();
    }
}

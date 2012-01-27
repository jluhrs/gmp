package edu.gemini.aspen.gmp.epicstostatus;

import edu.gemini.aspen.gmp.epicstostatus.generated.Channels;
import edu.gemini.aspen.gmp.epicstostatus.generated.SimpleChannelType;
import org.junit.Test;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class EpicsStatusServiceConfigurationTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/28/10
 */
public class EpicsToStatusConfigurationTest {
    private static final Logger LOG = Logger.getLogger(EpicsToStatusConfigurationTest.class.getName());
    public static final String xmlStr;

    public static final String xsdStr;


    static {
        BufferedReader in = new BufferedReader(new InputStreamReader(EpicsToStatusConfigurationTest.class.getResourceAsStream("../../../../../gmp-epics-to-status-mapping.xml")));
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

        in = new BufferedReader(new InputStreamReader(EpicsToStatusConfigurationTest.class.getResourceAsStream("../../../../../gmp-epics-to-status-mapping.xsd")));
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

    }

    @Test
    public void testBasic() {
        try {
            File xml = null;

            xml = File.createTempFile("EpicsTest", ".xml");

            File xsd = null;
            xsd = File.createTempFile("EpicsTest", ".xsd");

            FileWriter xmlWrt = new FileWriter(xml);
            FileWriter xsdWrt = new FileWriter(xsd);

            xmlWrt.write(xmlStr);
            xsdWrt.write(xsdStr);
            xmlWrt.close();
            xsdWrt.close();

            EpicsToStatusConfiguration ep = new EpicsToStatusConfiguration(xml.getPath(), xsd.getPath());


            Channels channels = ep.getSimulatedChannels();
            SimpleChannelType ch = (SimpleChannelType) channels.getSimpleChannelOrAlarmChannelOrHealthChannel().get(0);
            assertEquals("giapitest:statusitemint", ch.getStatusitem());
            assertEquals("giapitest:epicschannelint", ch.getEpicschannel());
            assertEquals(new Integer(0), ch.getIndex());

        } catch (IOException ex) {
            fail();
        }
    }

    @Test
    public void testNoIndex() {
        try {
            File xml = null;

            xml = File.createTempFile("EpicsTest", ".xml");

            File xsd = null;
            xsd = File.createTempFile("EpicsTest", ".xsd");

            FileWriter xmlWrt = new FileWriter(xml);
            FileWriter xsdWrt = new FileWriter(xsd);

            xmlWrt.write(xmlStr);
            xsdWrt.write(xsdStr);
            xmlWrt.close();
            xsdWrt.close();

            EpicsToStatusConfiguration ep = new EpicsToStatusConfiguration(xml.getPath(), xsd.getPath());


            Channels channels = ep.getSimulatedChannels();
            SimpleChannelType ch = (SimpleChannelType) channels.getSimpleChannelOrAlarmChannelOrHealthChannel().get(1);
            assertEquals("giapitest:giapialarmfloat", ch.getStatusitem());
            assertEquals("giapitest:epicsalarmfloat", ch.getEpicschannel());
            assertEquals(null, ch.getIndex());

        } catch (IOException ex) {
            fail();
        }
    }
}

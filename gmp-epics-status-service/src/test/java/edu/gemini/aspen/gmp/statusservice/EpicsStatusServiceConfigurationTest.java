package edu.gemini.aspen.gmp.statusservice;

import edu.gemini.aspen.gmp.statusservice.generated.Channels;
import edu.gemini.aspen.gmp.statusservice.generated.DataType;
import edu.gemini.aspen.gmp.statusservice.generated.SimpleChannelType;
import org.junit.Test;
import junit.framework.TestCase;
import sun.util.LocaleServiceProviderPool;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class EpicsStatusServiceConfigurationTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/28/10
 */
public class EpicsStatusServiceConfigurationTest extends TestCase{
    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceConfigurationTest.class.getName());
    public static final String xmlStr;

    public static final String xsdStr;


    static {
        BufferedReader in = new BufferedReader(new InputStreamReader(EpicsStatusServiceConfigurationTest.class.getResourceAsStream("../../../../../giapi-epics-status-mapping.xml")));
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

        in = new BufferedReader(new InputStreamReader(EpicsStatusServiceConfigurationTest.class.getResourceAsStream("../../../../../giapi-epics-status-mapping.xsd")));
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

            EpicsStatusServiceConfiguration ep = new EpicsStatusServiceConfiguration(xml.getPath(), xsd.getPath());
            SimpleChannelType ch = new SimpleChannelType();
            ch.setGiapiname("giapinameint");
            ch.setEpicsname("epicsnameint");
            ch.setType(DataType.INT);
            ch.setInitial("2");

            Channels channels =new Channels();
            channels.getSimpleChannelOrAlarmChannelOrHealthChannel().add(ch);


            Channels channels2 = ep.getSimulatedChannels();
            SimpleChannelType ch2=(SimpleChannelType)channels2.getSimpleChannelOrAlarmChannelOrHealthChannel().get(0);
            assertEquals(ch2.getGiapiname(),ch.getGiapiname());
            assertEquals(ch2.getEpicsname(), ch.getEpicsname());
            assertEquals(ch2.getType(),ch.getType());
            assertEquals(ChannelsHelper.getInitial(ch2),ChannelsHelper.getInitial(ch));

        } catch (IOException ex) {
            fail();
        }
    }
}

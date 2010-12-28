package edu.gemini.aspen.gmp.statusservice;

import edu.gemini.aspen.gmp.statusservice.osgi.Channels;
import edu.gemini.aspen.gmp.statusservice.osgi.ChannelsHelper;
import edu.gemini.aspen.gmp.statusservice.osgi.EpicsStatusServiceConfiguration;
import org.junit.Test;
import junit.framework.TestCase;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class EpicsStatusServiceConfigurationTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/28/10
 */
public class EpicsStatusServiceConfigurationTest extends TestCase{

    private String xmlStr = "<?xml version=\"1.0\"?>\n" +
            "<Channels xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "        xsi:noNamespaceSchemaLocation=\"giapi-epics-status-mapping.xsd\">\n" +
            "        <ChannelConfig>\n" +
            "                <giapiname>giapiname</giapiname>\n" +
            "                <epicsname>epicsname</epicsname>\n" +
            "                <type>INT</type>\n" +
            "                <initial>2</initial>\n" +
            "        </ChannelConfig>\n" +
            "        <ChannelConfig>\n" +
            "                <giapiname>giapiname2</giapiname>\n" +
            "                <epicsname>epicsname2</epicsname>\n" +
            "                <type>FLOAT</type>\n" +
            "                <initial>3</initial>\n" +
            "        </ChannelConfig>\n" +
            "        <ChannelConfig>\n" +
            "                <giapiname>giapiname3</giapiname>\n" +
            "                <epicsname>epicsname3</epicsname>\n" +
            "                <type>DOUBLE</type>\n" +
            "                <initial>3</initial>\n" +
            "        </ChannelConfig>\n" +
            "        <ChannelConfig>\n" +
            "                <giapiname>giapiname4</giapiname>\n" +
            "                <epicsname>epicsname4</epicsname>\n" +
            "                <type>STRING</type>\n" +
            "                <initial>hola</initial>\n" +
            "        </ChannelConfig>\n" +
            "</Channels>";
    private String xsdStr = "<?xml version=\"1.0\"?>\n" +
            "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "<xs:element name=\"Channels\">\n" +
            "        <xs:complexType>\n" +
            "        <xs:sequence>\n" +
            "                <xs:element name=\"ChannelConfig\" maxOccurs=\"unbounded\">\n" +
            "                        <xs:complexType>\n" +
            "                        <xs:sequence>\n" +
            "                                <xs:element name=\"giapiname\" type=\"xs:string\"/>\n" +
            "                                <xs:element name=\"epicsname\" type=\"xs:string\"/>\n" +
            "                                <xs:element name=\"type\" type=\"xs:string\"/>\n" +
            "                                <xs:element name=\"initial\" type=\"xs:string\"/>\n" +
            "                        </xs:sequence>\n" +
            "                        </xs:complexType>\n" +
            "                </xs:element>\n" +
            "        </xs:sequence>\n" +
            "        </xs:complexType>\n" +
            "</xs:element>\n" +
            "</xs:schema>";

    @Test
    public void testBasic() {
        try {
            File xml = null;

            xml = File.createTempFile("EpicsTest", "xml");

            File xsd = null;
            xsd = File.createTempFile("EpicsTest", "xsd");

            FileWriter xmlWrt = new FileWriter(xml);
            FileWriter xsdWrt = new FileWriter(xsd);

            xmlWrt.write(xmlStr);
            xsdWrt.write(xsdStr);
            xmlWrt.close();
            xsdWrt.close();

            EpicsStatusServiceConfiguration ep = new EpicsStatusServiceConfiguration(xml.getPath(), xsd.getPath());
            Channels.ChannelConfig ch = new Channels.ChannelConfig();
            ch.setGiapiname("giapiname");
            ch.setEpicsname("epicsname");
            ch.setType("INT");
            ch.setInitial("2");


            Channels.ChannelConfig ch2 = ep.getSimulatedChannels().get(0);
            assertEquals(ch2.getGiapiname(),ch.getGiapiname());
            assertEquals(ch2.getEpicsname(), ch.getEpicsname());
            assertEquals(ch2.getType(),ch.getType());
            assertEquals(ChannelsHelper.getInitial(ch2),ChannelsHelper.getInitial(ch));

        } catch (IOException ex) {
            fail();
        }
    }
}

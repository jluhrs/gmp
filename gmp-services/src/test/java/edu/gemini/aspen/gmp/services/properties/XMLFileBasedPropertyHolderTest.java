package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.gmp.services.GMPServicesTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLFileBasedPropertyHolderTest {
    @Test
    public void readProperties() {
        XMLFileBasedPropertyHolder propertyHolder = new XMLFileBasedPropertyHolder(GMPServicesTest.class.getResource("gmp-properties.xml").getFile());

        assertEquals(propertyHolder.getProperty("GMP_HOST_NAME"), "localhost");
        assertEquals(propertyHolder.getProperty("DHS_ANCILLARY_DATA_PATH"), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty("DHS_SCIENCE_DATA_PATH"), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty("DHS_INTERMEDIATE_DATA_PATH"), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty("DEFAULT"), "");
    }

}

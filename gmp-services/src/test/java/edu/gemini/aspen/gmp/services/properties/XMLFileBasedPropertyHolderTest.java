package edu.gemini.aspen.gmp.services.properties;

import edu.gemini.aspen.gmp.services.GMPServicesTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class XMLFileBasedPropertyHolderTest {
    @Test
    public void readProperties() {
        XMLFileBasedPropertyHolder propertyHolder = new XMLFileBasedPropertyHolder(GMPServicesTest.class.getResource("gmp-properties.xml").getFile());

        assertEquals(propertyHolder.getProperty(GmpProperties.GMP_HOST_NAME.name()), "localhost");
        assertEquals(propertyHolder.getProperty(GmpProperties.DHS_ANCILLARY_DATA_PATH.name()), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty(GmpProperties.DHS_SCIENCE_DATA_PATH.name()), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty(GmpProperties.DHS_PERMANENT_SCIENCE_DATA_PATH.name()), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty(GmpProperties.DHS_INTERMEDIATE_DATA_PATH.name()), "/home/anunez/tmp");
        assertEquals(propertyHolder.getProperty(GmpProperties.DEFAULT.name()), "");
    }

}

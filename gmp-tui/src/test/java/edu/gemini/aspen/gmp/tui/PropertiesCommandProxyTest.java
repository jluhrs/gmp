package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.gmp.services.PropertyHolder;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PropertiesCommandProxyTest {
    private PropertiesCommandProxy propertiesCommandProxy;
    private PropertyHolder propertyHolder;

    @Before
    public void setUp() throws Exception {
        propertyHolder = mock(PropertyHolder.class);
        propertiesCommandProxy = new PropertiesCommandProxy(propertyHolder);
    }

    @Test
    public void testPropertiesCall() {
        propertiesCommandProxy.properties();

        verify(propertyHolder, atLeastOnce()).getProperty(anyString());
    }
}

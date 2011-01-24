package edu.gemini.aspen.gmp.pcs.osgi;

import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.mockito.Mockito.*;

/**
 * Tests the activator, basically limiting itself to verify how the bundle is configured.
 * <p/>
 * @author cquiroz
 */
public class ActivatorTest {

    @Test
    public void testSimulationAsDefault() throws Exception {
        Activator activator = new Activator();
        BundleContext bundleContext = mock(BundleContext.class);

        // We simulate that we pass a null value so it must default to simulation mode
        when(bundleContext.getProperty("edu.gemini.aspen.gmp.pcs.simulation")).thenReturn(null);

        activator.start(bundleContext);

        // Verify that the property is retrieved
        verify(bundleContext).getProperty("edu.gemini.aspen.gmp.pcs.simulation");

        // Called when a ServiceTracker is created
        verify(bundleContext, times(1)).createFilter(anyString());
    }

}

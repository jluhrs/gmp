package edu.gemini.aspen.gmp.tcs.model.osgi;

import edu.gemini.aspen.gmp.tcs.osgi.Activator;
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
        when(bundleContext.getProperty("edu.gemini.aspen.gmp.tcs.simulation")).thenReturn(null);
        // In that case the file for simulation is required
        when(bundleContext.getProperty("edu.gemini.aspen.gmp.tcs.simulationData")).thenReturn("src/test/resources/edu/gemini/aspen/gmp/tcs/model/tcsCtx.data");

        activator.start(bundleContext);

        // Verify the properties are retrieved
        verify(bundleContext).getProperty("edu.gemini.aspen.gmp.tcs.simulation");
        verify(bundleContext).getProperty("edu.gemini.aspen.gmp.tcs.simulationData");
    }

}

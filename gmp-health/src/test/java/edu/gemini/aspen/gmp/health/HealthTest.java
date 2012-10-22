package edu.gemini.aspen.gmp.health;

import com.google.common.util.concurrent.AtomicDouble;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.util.jms.status.IStatusSetter;
import edu.gemini.aspen.gmp.top.Top;
import edu.gemini.aspen.gmp.top.TopImpl;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Tests for Health
 */
public class HealthTest {

    @Test
    public void testGood() throws Exception {
        Top top = new TopImpl("gmp", "gmp");
        IStatusSetter statusSetter = mock(IStatusSetter.class);
        BundlesDatabase bundlesDB = mock(BundlesDatabase.class);
        when(bundlesDB.getPercentageActive()).thenReturn(new AtomicDouble(1.0));
        Health service = new Health("health", top, statusSetter, bundlesDB);
        service.start();

        verify(statusSetter).setStatusItem(any(StatusItem.class));
    }

    @Test
    public void testWarning() throws Exception {
        Top top = new TopImpl("gmp", "gmp");
        IStatusSetter statusSetter = mock(IStatusSetter.class);
        BundlesDatabase bundlesDB = mock(BundlesDatabase.class);
        when(bundlesDB.getPercentageActive()).thenReturn(new AtomicDouble(0.7));
        Health service = new Health("health", top, statusSetter, bundlesDB);
        service.start();

        verify(statusSetter).setStatusItem(any(StatusItem.class));
    }
}

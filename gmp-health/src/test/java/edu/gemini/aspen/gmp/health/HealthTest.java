package edu.gemini.aspen.gmp.health;

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
    public void testBasic() throws Exception {
        Top top = new TopImpl("gmp", "gmp");
        IStatusSetter statusSetter = mock(IStatusSetter.class);
        Health service = new Health("health", top, statusSetter);
        service.start();

        verify(statusSetter).setStatusItem(any(StatusItem.class));
    }
}

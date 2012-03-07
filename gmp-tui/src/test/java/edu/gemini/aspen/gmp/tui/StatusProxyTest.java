package edu.gemini.aspen.gmp.tui;

import edu.gemini.aspen.giapi.status.StatusDatabaseService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StatusProxyTest {
    private StatusProxy statusProxy;
    private StatusDatabaseService statusDB;

    @Before
    public void setUp() throws Exception {
        statusDB = mock(StatusDatabaseService.class);
        statusProxy = new StatusProxy(statusDB);
    }

    @Test
    public void verifyScope() {
        assertEquals("gmp", statusProxy.SCOPE);
    }

    @Test
    public void verifyFunctions() {
        assertEquals("statusnames", statusProxy.FUNCTIONS[0]);
        assertEquals("status", statusProxy.FUNCTIONS[1]);
    }

    @Test
    public void testStatusNamesCall() {
        statusProxy.statusnames();

        verify(statusDB).getStatusNames();
    }

    @Test
    public void testStatusCall() {
        statusProxy.status();

        verify(statusDB).getAll();
    }
}

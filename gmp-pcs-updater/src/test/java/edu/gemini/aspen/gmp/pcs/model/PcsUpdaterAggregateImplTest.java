package edu.gemini.aspen.gmp.pcs.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


/**
 * Test for the PcsUpdaterAggregateImpl class
 */
public class PcsUpdaterAggregateImplTest {
    private PcsUpdater u1, u2, u3;
    private PcsUpdaterAggregateImpl _composite;
    private Double[] values = new Double [] {1.5, 6.7};

    @Before
    public void setUp() {
        u1 = mock(PcsUpdater.class);
        u2 = mock(PcsUpdater.class);
        u3 = mock(PcsUpdater.class);
        _composite = new PcsUpdaterAggregateImpl();
    }

    @Test
    public void testRegisterUpdaters() throws PcsUpdaterException {
        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);

        PcsUpdate update = new PcsUpdate(values);

        //Send the update, and see if the three updaters
        //should have been gotten the update.
        _composite.update(update);

        ArgumentCaptor<PcsUpdate> updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(u1).update(updateCapture.capture());
        assertEquals(updateCapture.getValue(), new PcsUpdate(values));

        updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(u2).update(updateCapture.capture());
        assertEquals(updateCapture.getValue(), new PcsUpdate(values));

        updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(u3).update(updateCapture.capture());
        assertEquals(updateCapture.getValue(), new PcsUpdate(values));
    }

    @Test
    public void tesUnRegisterUpdaters() throws PcsUpdaterException {
        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);
        _composite.unregisterUpdater(u1);

        PcsUpdate update = new PcsUpdate(values);
        _composite.update(update);

        verifyZeroInteractions(u1);
        ArgumentCaptor<PcsUpdate> updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(u2).update(updateCapture.capture());
        assertEquals(updateCapture.getValue(), new PcsUpdate(values));

        updateCapture = ArgumentCaptor.forClass(PcsUpdate.class);
        verify(u3).update(updateCapture.capture());
        assertEquals(updateCapture.getValue(), new PcsUpdate(values));
    }

}

package edu.gemini.aspen.gmp.pcs.model;

import edu.gemini.aspen.gmp.pcs.test.TestPcsUpdater;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Test for the PcsUpdaterCompositeImpl class
 */
public class PcsUpdaterCompositeImplTest {

    private final TestPcsUpdater u1, u2, u3;

    private PcsUpdaterCompositeImpl _composite;

    public PcsUpdaterCompositeImplTest() {
        u1 = new TestPcsUpdater();
        u2 = new TestPcsUpdater();
        u3 = new TestPcsUpdater();
    }

    @Before
    public void setUp() {

        u1.reset();
        u2.reset();
        u3.reset();
        _composite = new PcsUpdaterCompositeImpl();

    }


    @Test
    public void testRegisterUpdaters() {

        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);

        Double[] values = new Double [] {
                1.5,
                6.7
        };
        PcsUpdate update = new PcsUpdate(values);

        //Send the update, and see if the three updaters
        //should have been gottent the update.
        synchronized (u1) {
            try {
                _composite.update(update);
                u1.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            } catch (PcsUpdaterException e) {
                fail("Failed to send update to PcsComposite");
            }
        }

        assertArrayEquals(values, u1.getUpdate().getZernikes() );

        synchronized (u2) {
            try {
                u2.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }

        assertArrayEquals(values, u2.getUpdate().getZernikes() );

        synchronized (u3) {
            try {
                u3.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }
        assertArrayEquals(values, u3.getUpdate().getZernikes() );


    }

    @Test
    public void tesUnRegisterUpdaters() {

        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);
        _composite.unregisterUpdater(u1);

        Double[] values = new Double [] {
                1.5,
                6.7
        };

        PcsUpdate update = new PcsUpdate(values);

        try {
            _composite.update(update);
        } catch (PcsUpdaterException e) {
            fail("Failed to send update to PcsComposite");
        }


        //the first updaters should have not been gotten the update.
        synchronized (u1) {
            try {
                u1.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }

        assertNull(u1.getUpdate() );

        //but the other two should have
        synchronized (u2) {
            try {
                u2.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }

        assertArrayEquals(values, u2.getUpdate().getZernikes() );

        synchronized (u3) {
            try {
                u3.wait(100);
            } catch (InterruptedException e) {
                fail("Updater interrupted");
            }
        }
        assertArrayEquals(values, u3.getUpdate().getZernikes() );


    }




}

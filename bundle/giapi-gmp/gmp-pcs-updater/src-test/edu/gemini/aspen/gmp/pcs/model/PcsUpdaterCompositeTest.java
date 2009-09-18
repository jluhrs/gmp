package edu.gemini.aspen.gmp.pcs.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.junit.Assert.assertArrayEquals;
import edu.gemini.aspen.gmp.pcs.test.TestPcsUpdater;


/**
 * Test for the PcsUpdaterComposite class
 */
public class PcsUpdaterCompositeTest {

    private final TestPcsUpdater u1, u2, u3;

    private PcsUpdaterComposite _composite;

    public PcsUpdaterCompositeTest() {
        u1 = new TestPcsUpdater();
        u2 = new TestPcsUpdater();
        u3 = new TestPcsUpdater();
    }

    @Before
    public void setUp() {

        u1.reset();
        u2.reset();
        u3.reset();
        _composite = new PcsUpdaterComposite();

    }


    @Test
    public void testRegisterUpdaters() {

        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);

        PcsUpdate update = new PcsUpdate();

        Double[] values = new Double [] {
                1.5,
                6.7
        };

        for (Double value: values) {
            update.addZernike(value);
        }

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
    public void tesUnregisterUpdaters() {

        _composite.registerUpdater(u1);
        _composite.registerUpdater(u2);
        _composite.registerUpdater(u3);
        _composite.unregisterUpdater(u1);

        PcsUpdate update = new PcsUpdate();

        Double[] values = new Double [] {
                1.5,
                6.7
        };

        for (Double value: values) {
            update.addZernike(value);
        }

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

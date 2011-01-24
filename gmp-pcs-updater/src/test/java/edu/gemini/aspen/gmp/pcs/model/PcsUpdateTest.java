package edu.gemini.aspen.gmp.pcs.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import com.gargoylesoftware.base.testing.EqualsTester;

/**
 * Test class for the PcsUpdate class
 */
public class PcsUpdateTest {

    private PcsUpdate u1, u2, u3;

    @Before
    public void setUp() {
        u1 = new PcsUpdate();
        u1.addZernike(1.0);
        u1.addZernike(3.0);

        u2 = new PcsUpdate();
        u2.addZernike(1.0);
        u2.addZernike(3.0);

        u3 = new PcsUpdate();
        u3.addZernike(4.0);
        u3.addZernike(7.0);

    }

    @Test
    public void testEquals() {
        new EqualsTester(u1, u2, u3, null);
    }

    @Test
    public void testGetZernikes() {

        Double[] d = new Double[]{
                1.0,
                3.0
        };

        assertArrayEquals(d, u1.getZernikes());

        d = new Double[] {
                4.0,
                7.0
        };

        assertArrayEquals(d, u3.getZernikes());

    }


}

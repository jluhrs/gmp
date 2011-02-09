package edu.gemini.aspen.gmp.pcs.model;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * Test class for the PcsUpdate class
 */
public class PcsUpdateTest {

    private PcsUpdate u1, u2, u3;

    @Before
    public void setUp() {
        u1 = new PcsUpdate(new Double[] {1.0, 3.0});
        u2 = new PcsUpdate(new Double[] {1.0, 3.0});
        u3 = new PcsUpdate(new Double[] {4.0, 7.0});
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

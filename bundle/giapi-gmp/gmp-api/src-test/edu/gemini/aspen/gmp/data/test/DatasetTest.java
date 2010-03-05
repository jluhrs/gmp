package edu.gemini.aspen.gmp.data.test;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.data.Dataset;
import com.gargoylesoftware.base.testing.EqualsTester;

/**
 * Unit tests for the Dataset class
 */
public class DatasetTest {

    private Dataset d1;
    private Dataset d2;
    private Dataset d3;

    @Before
    public void setUp() {
        d1 = new Dataset("test");
        d2 = new Dataset("test");
        d3 = new Dataset("other");
    }

    @Test
    public void testEquals() {
        new EqualsTester(d1, d2, d3, null);
    }

    @Test
    public void testInitialization() {
        assertEquals("test", d1.getName());
        assertEquals("other", d3.getName());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testWrongInitialization() {
        new Dataset(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyInitialization() {
        new Dataset("");
    }

    
}

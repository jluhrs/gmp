package edu.gemini.aspen.giapi.data;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the DataLabel class
 */
public class DataLabelTest {

    private DataLabel d1;
    private DataLabel d2;
    private DataLabel d3;

    @Before
    public void setUp() {
        d1 = new DataLabel("test");
        d2 = new DataLabel("test");
        d3 = new DataLabel("other");
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
        new DataLabel(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEmptyInitialization() {
        new DataLabel("");
    }
    
}

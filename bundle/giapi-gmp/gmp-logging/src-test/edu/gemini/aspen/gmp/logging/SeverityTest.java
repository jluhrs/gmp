package edu.gemini.aspen.gmp.logging;


import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Test class for the Severity enumerated type
 */
public class SeverityTest {

    @Test
    public void testGetSeverityByCode() {

        assertEquals(Severity.INFO, Severity.getSeverityByCode(1));
        assertEquals(Severity.WARNING, Severity.getSeverityByCode(2));
        assertEquals(Severity.SEVERE, Severity.getSeverityByCode(3));
    }

    @Test
    (expected = LoggingException.class)
    public void testInvalidCodesForSeverity0() {
        Severity.getSeverityByCode(0);
    }

    @Test
    (expected = LoggingException.class)
    public void testInvalidCodesForSeverity4()  {
        Severity.getSeverityByCode(4);
    }

    @Test
    public void testNames() {
        assertEquals("INFO", Severity.INFO.toString());
        assertEquals("SEVERE", Severity.SEVERE.toString());
        assertEquals("WARNING", Severity.WARNING.toString());


    }



}

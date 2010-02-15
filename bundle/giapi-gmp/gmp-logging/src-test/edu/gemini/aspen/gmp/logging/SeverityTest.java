package edu.gemini.aspen.gmp.logging;


import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Test class for the Severity enumerated type
 */
public class SeverityTest {

    @Test
    public void testGetSeverityByCode() {

        try {
            assertEquals(Severity.INFO, Severity.getSeverityByCode(1));
            assertEquals(Severity.WARNING, Severity.getSeverityByCode(2));
            assertEquals(Severity.SEVERE, Severity.getSeverityByCode(3));
        } catch (LoggingException e) {
            fail("Unexpected Exception :" + e.getMessage());
        }
    }

    @Test
    (expected = LoggingException.class)
    public void testInvalidCodesForSeverity0() throws LoggingException {
        Severity.getSeverityByCode(0);
    }

    @Test
    (expected = LoggingException.class)
    public void testInvalidCodesForSeverity4() throws LoggingException {
        Severity.getSeverityByCode(4);
    }



}

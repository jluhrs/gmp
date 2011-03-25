package edu.gemini.giapi.tool.log;

import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import static org.junit.Assert.assertTrue;

public class GiapiTesterFormatterTest {
    @Test
    public void testFormat() {
        String message = "Message";
        LogRecord record = new LogRecord(Level.FINE, message);

        assertTrue(new GiapiTesterFormatter().format(record).startsWith(message));
    }
}

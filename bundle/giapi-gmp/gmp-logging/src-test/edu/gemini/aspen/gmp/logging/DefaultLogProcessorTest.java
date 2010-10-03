package edu.gemini.aspen.gmp.logging;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the Default Log Processor
 */
public class DefaultLogProcessorTest {


    DefaultLogProcessor _processor;

    @Before
    public void setUp() {
        _processor = new DefaultLogProcessor();
    }

    @Test
    public void nullLogMessage() {

        _processor.processLogMessage(null);

        assertNull(_processor.getLastMessage());
    }

    @Test
    public void emptyLogMessage() {

        _processor.processLogMessage(new TestLogMessage(null, null));
        assertNull(_processor.getLastMessage());
    }

    @Test
    public void infoLogMessage() {


        LogMessage msg = new TestLogMessage(Severity.INFO, "Info Message");

        _processor.processLogMessage(msg);

        assertEquals(Severity.INFO, _processor.getLastMessage().getSeverity());

    }

    @Test
    public void warningLogMessage() {

        LogMessage msg = new TestLogMessage(Severity.WARNING, "Warning Message");

        _processor.processLogMessage(msg);

        assertEquals(Severity.WARNING, _processor.getLastMessage().getSeverity());


    }

    @Test
    public void errorLogMessage() {
        LogMessage msg = new TestLogMessage(Severity.SEVERE, "Error Message");

        _processor.processLogMessage(msg);

        assertEquals(Severity.SEVERE, _processor.getLastMessage().getSeverity());

    }

    @Test
    public void logMessageWithNullContent() {
        LogMessage msg = new TestLogMessage(Severity.INFO, null);

        _processor.processLogMessage(msg);

        assertEquals(Severity.INFO, _processor.getLastMessage().getSeverity());

    }

}

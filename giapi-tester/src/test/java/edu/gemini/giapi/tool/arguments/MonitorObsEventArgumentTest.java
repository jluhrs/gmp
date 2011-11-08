package edu.gemini.giapi.tool.arguments;

import org.junit.Test;

import static org.junit.Assert.*;

public class MonitorObsEventArgumentTest {
    @Test
    public void basicPropertiesTests() {
        MonitorObsEventArgument monitorObsEventArgument = new MonitorObsEventArgument();

        assertFalse(monitorObsEventArgument.requireParameter());
        assertEquals("obsEvents", monitorObsEventArgument.getKey());
    }
}

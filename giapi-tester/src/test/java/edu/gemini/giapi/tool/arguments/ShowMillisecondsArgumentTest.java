package edu.gemini.giapi.tool.arguments;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ShowMillisecondsArgumentTest {
    @Test
    public void basicPropertiesTests() {
        ShowMillisecondsArgument argument = new ShowMillisecondsArgument();

        assertFalse(argument.requireParameter());
        assertEquals("millis", argument.getKey());
        assertEquals(true, argument.getExpectedValue());
        argument.parseParameter("anything");
        assertEquals(true, argument.getExpectedValue());
    }
}

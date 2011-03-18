package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.Activity;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ActivityArgumentTest {
    @Test
    public void basicPropertiesTests() {
        ActivityArgument activityArgument = new ActivityArgument();
        activityArgument.parseParameter("PRESET");

        assertTrue(activityArgument.requireParameter());
        assertFalse(activityArgument.getInvalidArgumentMsg().isEmpty());

        assertEquals(Activity.PRESET, activityArgument.getActivity());
    }
}

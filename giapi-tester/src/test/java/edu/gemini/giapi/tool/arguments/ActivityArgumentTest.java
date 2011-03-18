package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActivityArgumentTest {
    @Test
    public void basicPropertiesTests() {
        ActivityArgument activityArgument = new ActivityArgument();
        activityArgument.parseParameter("PRESET");

        assertTrue(activityArgument.requireParameter());
        assertEquals(Activity.PRESET, activityArgument.getActivity());
    }
}

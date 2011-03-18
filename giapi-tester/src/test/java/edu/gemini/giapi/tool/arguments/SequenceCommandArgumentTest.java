package edu.gemini.giapi.tool.arguments;

import edu.gemini.aspen.giapi.commands.SequenceCommand;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SequenceCommandArgumentTest {
    @Test
    public void basicPropertiesTests() {
        SequenceCommandArgument sequenceCommandArgument = new SequenceCommandArgument();
        sequenceCommandArgument.parseParameter("PARK");

        assertTrue(sequenceCommandArgument.requireParameter());

        assertEquals(SequenceCommand.PARK, sequenceCommandArgument.getSequenceCommand());
    }
}

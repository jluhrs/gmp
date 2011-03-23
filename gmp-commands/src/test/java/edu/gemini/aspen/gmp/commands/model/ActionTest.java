package edu.gemini.aspen.gmp.commands.model;

import com.gargoylesoftware.base.testing.EqualsTester;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.CommandSender;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test the Action Class. 
 */
public class ActionTest {
    private Action a1, a2, a3;

    @Before
    public void setUp() {

        CompletionListener listener1 = new CompletionListenerMock();
        Configuration config1 = emptyConfiguration();
        a1 = new Action(new Command(SequenceCommand.ABORT,
                Activity.PRESET,
                config1),
                listener1);
        //the only way to create two Actions equals is to explicitly
        //set up the Action ID. This cannot be done outside the package.
        a2 = new Action(a1.getId(),
                new Command(SequenceCommand.ABORT,
                Activity.PRESET,
                config1),
                listener1, CommandSender.DEFAULT_COMMAND_RESPONSE_TIMEOUT);

        //Though this action looks "equals", it's not as the action id
        //should change.
        a3 = new Action(new Command(SequenceCommand.ABORT,
                Activity.PRESET,
                config1),
                listener1);
    }

    @Test
    public void testEquals() {
        new EqualsTester(a1, a2, a3, null);
    }

    @Test
    public void alwaysDifferent() {
        assertFalse(a1.equals(a3));
    }

    @Test
    public void testCompare() {
        assertTrue(a1.compareTo(a3) < 0);
        assertTrue(a2.compareTo(a3) < 0);
    }

    @Test
    public void testAccessSequenceCommand() {
        assertEquals(SequenceCommand.ABORT, a1.getCommand().getSequenceCommand());
    }

    @Test
    public void testAccessActivity() {
        assertEquals(Activity.PRESET, a1.getCommand().getActivity());
    }

    @Test
    public void testAccessConfiguration() {
        assertEquals(emptyConfiguration(), a1.getCommand().getConfiguration());
    }
}

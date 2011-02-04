package edu.gemini.aspen.gmp.commands.model;

import com.gargoylesoftware.base.testing.EqualsTester;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.Configuration;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.test.TestCompletionListener;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.*;


/**
 * Test the Action Class. 
 */
public class ActionTest {


    private Action a1, a2, a3, a4;



    @Before
    public void setUp() {

        CompletionListener listener1 = new TestCompletionListener();
        Configuration config1 = emptyConfiguration();
        a1 = new Action(SequenceCommand.ABORT,
                Activity.PRESET,
                config1,
                listener1);

        a2 = new Action(SequenceCommand.ABORT,
                Activity.PRESET,
                config1,
                listener1);

        a3 = new Action(SequenceCommand.ABORT,
                Activity.PRESET_START,
                config1,
                listener1);

        a4 = a1.mutate(a1.getSequenceCommand(), a1.getActivity(), a1.getConfiguration(), a1.getCompletionListener());

    }

    @Test
    public void testEquals() {
        new EqualsTester(a1, a4, a2, null);
    }

    @Test
    public void alwaysDifferent() {
        assertFalse(a1.equals(a2));
    }

    @Test
    public void testCompare() {
        assertTrue(a1.compareTo(a2) < 0);
        assertTrue(a2.compareTo(a3) < 0);
        assertTrue(a1.compareTo(a3) < 0);
    }

    @Test
    public void testMutate() {

        Action a = a1.mutate(SequenceCommand.DATUM, Activity.START, null, a1.getCompletionListener());

        assertEquals(SequenceCommand.DATUM, a.getSequenceCommand());
        assertEquals(Activity.START, a.getActivity());
        assertEquals(null, a.getConfiguration());
        assertEquals(a1.getCompletionListener(), a.getCompletionListener());

        //finally check that both objects are equals now
        assertEquals(a1, a);


    }



}

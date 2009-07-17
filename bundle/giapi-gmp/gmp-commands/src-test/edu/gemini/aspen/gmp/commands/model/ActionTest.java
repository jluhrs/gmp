package edu.gemini.aspen.gmp.commands.model;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.commands.test.TestCompletionListener;


/**
 * Test the Action Class. 
 */
public class ActionTest {


    private Action a1, a2, a3;



    @Before
    public void setUp() {

        CompletionListener listener1 = new TestCompletionListener();
        Configuration config1 = new DefaultConfiguration();
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



}

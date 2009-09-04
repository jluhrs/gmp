package edu.gemini.aspen.gmp.commands.model;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.commands.test.TestCompletionListener;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

import java.util.List;
import java.util.ArrayList;

/**
 * Test suite for the Action Manager class. 
 */
public class ActionManagerTest {

    private ActionManager manager;

    private List<Action> actions;

    private static final int TOTAL_ACTIONS = 10;

    @Before
    public void setUp() {
        manager = new ActionManager();
        manager.start();
        actions = new ArrayList<Action>();

        Configuration config1 = new DefaultConfiguration();

        for (int i = 0; i < TOTAL_ACTIONS; i++) {
            actions.add(new Action(SequenceCommand.ABORT,
                    Activity.PRESET,
                    config1,
                    new TestCompletionListener()));
        }

    }

    @After
    public void tearDown() {
        manager.stop();
    }

    /**
     * Basic test. One action being monitored, and completion info.
     * received for that action
     */
    @Test
    public void testOneActionOneCompletion() {
        Action a1 = actions.get(0);

        manager.registerAction(a1);

        manager.registerCompletionInformation(a1.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));


        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(5000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        TestCompletionListener cl = (TestCompletionListener) a1.getCompletionListener();
        assertTrue(cl.wasInvoked());
    }

    /**
     * This test will verify the locking mechanism in the manager works
     */
    @Test
    public void testLock() {
        Action a1 = actions.get(0);

        manager.registerAction(a1);

        //lock the manager, so it should not update the listener...
        manager.lock();

        manager.registerCompletionInformation(a1.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));


        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        TestCompletionListener cl = (TestCompletionListener) a1.getCompletionListener();
        assertFalse(cl.wasInvoked());

        //unlock the manager
        manager.unlock();

        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        assertTrue(cl.wasInvoked());

    }


    /**
     * Validates that completion info for an action will trigger the listeners
     * of all the actions with lower action ID.
     */
    @Test
    public void testMultiplePendingActions() {

        for (Action a : actions) {
            manager.registerAction(a);
        }

        //trigger the last to one action...
        manager.registerCompletionInformation(actions.get(TOTAL_ACTIONS - 1 - 1).getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        //and give the listeners a chance to run...

        for (Action a : actions) {
            synchronized (a.getCompletionListener()) {
                try {
                    a.getCompletionListener().wait(1000);
                } catch (InterruptedException e) {
                    fail("Thread interrupted");
                }
            }
        }

        for (int i = 0; i < TOTAL_ACTIONS - 1; i++) {
            assertTrue(((TestCompletionListener) ((actions.get(i).getCompletionListener()))).wasInvoked());
        }
        assertFalse(((TestCompletionListener) ((actions.get(TOTAL_ACTIONS - 1).getCompletionListener()))).wasInvoked());

    }

    /**
     * Test the correct behavior of receiving info about an action that
     * is not being tracked.
     */
    @Test
    public void testInvalidAction() {

        Action a1 = actions.get(0);

        manager.registerCompletionInformation(a1.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        assertFalse(((TestCompletionListener) ((a1.getCompletionListener()))).wasInvoked());

    }

    /**
     * Test case to make sure if completion information is received more than once,
     * it won't trigger the completion listener twice
     */
    @Test
    public void testDuplicatedAction() {

        Action a1 = actions.get(0);

        manager.registerAction(a1);

        manager.registerCompletionInformation(a1.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        assertTrue(((TestCompletionListener) ((a1.getCompletionListener()))).wasInvoked());


        //now, receive completion again...
        ((TestCompletionListener) ((a1.getCompletionListener()))).reset();

        manager.registerCompletionInformation(a1.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));
        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }
        assertFalse(((TestCompletionListener) ((a1.getCompletionListener()))).wasInvoked());
    }


    /**
     * Test case for actions that are not yet produced by the system.
     * It shouldn't invoke any handlers since this is a problem in the
     * code.
     */
    @Test
    public void testFutureAction() {
        Action a1 = actions.get(0);

        manager.registerAction(a1);

        //receive completion info for an ID we haven't produced..
        manager.registerCompletionInformation(actions.get(TOTAL_ACTIONS - 1).getId() + 1,
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        //see if this triggers action #1.
        synchronized (a1.getCompletionListener()) {
            try {
                a1.getCompletionListener().wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }
        //it shouldn't have been called. 
        assertFalse(((TestCompletionListener) ((a1.getCompletionListener()))).wasInvoked());


    }

    /**
     * Test the case when the action does not have a listener associated.
     */
    @Test
    public void testNoListener() {
        Action a = new Action(SequenceCommand.ABORT, Activity.PRESET,
                new DefaultConfiguration(), null);
        manager.registerAction(a);

        manager.registerCompletionInformation(a.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        //if everything went fine, we should be able to process other actions..
        //since otherwise the null listener should have caused a NPE that
        //would have stopped the execution loop

        testOneActionOneCompletion();
        tearDown();

        setUp();
        testMultiplePendingActions();
    }


    /**
     * This test verifies the handling of completion information
     * for the APPLY sequence command, which is slightly different
     * from all the other sequence commands in the sense it
     * can be handled by multiple handler, hence the action manager
     * can receive multiple responses. This test verifies the simpler
     * case when only one handler replies.
     */

    @Test
    public void testCompletionForApplySequenceCommand() {
        TestCompletionListener cl = new TestCompletionListener();

        Action a = new Action(SequenceCommand.APPLY,
                Activity.PRESET_START,
                new DefaultConfiguration(),
                cl);

        manager.registerAction(a);

        //An action started has been registered.
        manager.increaseRequiredResponses(a);

        manager.registerCompletionInformation(a.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        //the completion listener should have been called.
        synchronized (a.getCompletionListener()) {
            try {
                a.getCompletionListener().wait(5000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }
        assertTrue(cl.wasInvoked());
    }

    /**
     * Test the handling of multiple completion information for the same
     * action ID. This happens for the APPLY sequence command. If an
     * APPLY configuration is handled by multiple handlers, then
     * the configuration will be split and the several handlers
     * will reply completion information to the SAME action ID.
     */
    @Test
    public void testMultipleCompletionForApplySequenceCommand() {


        TestCompletionListener cl = new TestCompletionListener();

        Action a = new Action(SequenceCommand.APPLY,
                Activity.PRESET_START,
                new DefaultConfiguration(),
                cl);

        manager.registerAction(a);

        //two handlers will be expected..
        manager.increaseRequiredResponses(a); manager.increaseRequiredResponses(a);


        cl.reset();
        manager.registerCompletionInformation(a.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        //the completion listener should not have been called.

        synchronized (a.getCompletionListener()) {
            try {
                a.getCompletionListener().wait(5000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        assertFalse(cl.wasInvoked());

        cl.reset();

        manager.registerCompletionInformation(a.getId(),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        synchronized (a.getCompletionListener()) {
            try {
                a.getCompletionListener().wait(5000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }

        assertTrue(cl.wasInvoked());
    }
}

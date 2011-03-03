package edu.gemini.aspen.gmp.commands.model;

import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.CompletionListener;
import edu.gemini.aspen.giapi.commands.HandlerResponse;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.gmp.commands.test.CompletionListenerMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.*;

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
        actions = Lists.newArrayList();

        for (int i = 0; i < TOTAL_ACTIONS; i++) {
            actions.add(new Action(SequenceCommand.ABORT,
                    Activity.PRESET,
                    emptyConfiguration(),
                    new CompletionListenerMock()));
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
        Action action = actions.get(0);

        manager.registerAction(action);

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        waitForListener(action.getCompletionListener(), 5000);

        CompletionListenerMock cl = (CompletionListenerMock) action.getCompletionListener();
        assertTrue(cl.wasInvoked());
    }

    private void waitForListener(CompletionListener listener, int maxTime) {
        synchronized (listener) {
            try {
                listener.wait(maxTime);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }
    }

    /**
     * This test will verify the locking mechanism in the manager works
     */
    @Test
    public void testLock() {
        Action action = actions.get(0);

        manager.registerAction(action);

        //lock the manager, so it should not update the listener...
        manager.lock();

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        waitForListener(action.getCompletionListener(), 1000);

        CompletionListenerMock cl = (CompletionListenerMock) action.getCompletionListener();
        assertFalse(cl.wasInvoked());

        //unlock the manager
        manager.unlock();

        waitForListener(action.getCompletionListener(), 1000);

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
                HandlerResponse.COMPLETED);

        //and give the listeners a chance to run...
        for (Action a : actions) {
            waitForListener(a.getCompletionListener(), 1000);
        }

        for (int i = 0; i < TOTAL_ACTIONS - 1; i++) {
            assertTrue(((CompletionListenerMock) ((actions.get(i).getCompletionListener()))).wasInvoked());
        }
        assertFalse(((CompletionListenerMock) ((actions.get(TOTAL_ACTIONS - 1).getCompletionListener()))).wasInvoked());

    }

    /**
     * Test the correct behavior of receiving info about an action that
     * is not being tracked.
     */
    @Test
    public void testInvalidAction() {
        Action action = actions.get(0);

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        waitForListener(action.getCompletionListener(), 1000);
        assertFalse(((CompletionListenerMock) ((action.getCompletionListener()))).wasInvoked());

    }

    /**
     * Test case to make sure if completion information is received more than once,
     * it won't trigger the completion listener twice
     */
    @Test
    public void testDuplicatedAction() {
        Action action = actions.get(0);

        manager.registerAction(action);

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        waitForListener(action.getCompletionListener(), 1000);

        assertTrue(((CompletionListenerMock) ((action.getCompletionListener()))).wasInvoked());

        //now, receive completion again...
        ((CompletionListenerMock) ((action.getCompletionListener()))).reset();

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);
        waitForListener(action.getCompletionListener(), 1000);
        assertFalse(((CompletionListenerMock) ((action.getCompletionListener()))).wasInvoked());
    }


    /**
     * Test case for actions that are not yet produced by the system.
     * It shouldn't invoke any handlers since this is a problem in the
     * code.
     */
    @Test
    public void testFutureAction() {
        Action action = actions.get(0);

        manager.registerAction(action);

        //receive completion info for an ID we haven't produced..
        manager.registerCompletionInformation(actions.get(TOTAL_ACTIONS - 1).getId() + 1,
                HandlerResponse.COMPLETED);

        //see if this triggers action #1.
        waitForListener(action.getCompletionListener(), 1000);
        //it shouldn't have been called.
        assertFalse(((CompletionListenerMock) ((action.getCompletionListener()))).wasInvoked());
    }

    /**
     * Test the case when the action does not have a listener associated.
     */
    @Test
    public void testNoListener() {
        Action action = new Action(SequenceCommand.ABORT, Activity.PRESET,
                emptyConfiguration(), null);
        manager.registerAction(action);

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

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
        CompletionListenerMock cl = new CompletionListenerMock();

        Action action = new Action(SequenceCommand.APPLY,
                Activity.PRESET_START,
                emptyConfiguration(),
                cl);

        manager.registerAction(action);

        //An action started has been registered.
        manager.increaseRequiredResponses(action);

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        //the completion listener should have been called.
        waitForListener(action.getCompletionListener(), 5000);
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
        CompletionListenerMock cl = new CompletionListenerMock();

        Action action = new Action(SequenceCommand.APPLY,
                Activity.PRESET_START,
                emptyConfiguration(),
                cl);

        manager.registerAction(action);

        //two handlers will be expected..
        manager.increaseRequiredResponses(action); manager.increaseRequiredResponses(action);


        cl.reset();
        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        //the completion listener should not have been called.
        waitForListener(action.getCompletionListener(), 5000);

        assertFalse(cl.wasInvoked());

        cl.reset();

        manager.registerCompletionInformation(action.getId(),
                HandlerResponse.COMPLETED);

        waitForListener(action.getCompletionListener(), 5000);

        assertTrue(cl.wasInvoked());
    }
}

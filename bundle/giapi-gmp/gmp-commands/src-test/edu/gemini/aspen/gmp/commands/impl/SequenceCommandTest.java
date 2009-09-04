package edu.gemini.aspen.gmp.commands.impl;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import edu.gemini.aspen.gmp.commands.api.*;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.test.TestActionSender;
import edu.gemini.aspen.gmp.commands.test.TestSequenceCommandExecutor;
import edu.gemini.aspen.gmp.commands.test.TestCompletionListener;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;


/**
 * A Class to test Sequence Commnads. It will excercise
 * the Command Sender and Command Updater implementations
 */
public class SequenceCommandTest {

    private CommandSender cs;
    private CommandUpdater cu;

    private ActionManager actionManager;


    private TestActionSender sender; //A test action sender that does not use the network

    private TestSequenceCommandExecutor executor; //a simplified executor, that don't take into account the type of action.

    private final TestCompletionListener completionListener = new TestCompletionListener();

    @Before
    public void setUp() {

        actionManager = new ActionManager();
        actionManager.start();

        sender = new TestActionSender();

        cu = new CommandUpdaterImpl(actionManager);

        executor = new TestSequenceCommandExecutor(cu, completionListener);

        cs = new CommandSenderImpl(actionManager, sender, executor);



        completionListener.reset();

    }


    @After
    public void tearDown() {
        actionManager.stop();
    }

    /**
     * Test of a command that completes immediately, so the answer
     * returned to the client is COMPLETED, ERROR or ACCEPTED.
     */
    @Test
    public void testInmmediateCommand() {


        HandlerResponse[] answers = new HandlerResponse[]{
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED),
                HandlerResponseImpl.create(HandlerResponse.Response.ERROR),
                HandlerResponseImpl.create(HandlerResponse.Response.ACCEPTED),
        };

        for (HandlerResponse r : answers) {

            sender.defineAnswer(r);
            completionListener.reset();

            HandlerResponse response = cs.sendSequenceCommand(
                    SequenceCommand.ABORT,
                    Activity.PRESET,
                    completionListener
            );

            assertEquals(response, r);

            //let's fake completion information...(we shouldn't receive this, since
            //the action completed immediately). If we do, there is something
            //wrong in the instrument code since it's  sending completion
            //info for actions that did not "STARTED".
            //The code will generate WARNING Messages, but that's okay
            HandlerResponse r1 = HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);
            cu.updateOcs(Action.getCurrentId(), r1);


            //now, make sure the completion listener is not triggered, since
            //this action completed immediately.

            synchronized (completionListener) {
                try {
                    completionListener.wait(1000);
                } catch (InterruptedException e) {
                    fail("Thread interrupted");
                }
            }

            //the listener should not have been invoked
            assertFalse(completionListener.wasInvoked());
        }

    }

    /**
     * Test a command that takes time to complete. So the first answer is
     * STARTED and then we receive completion information as "COMPLETED".
     * 
     * 
     */
    @Test
    public void testLongCommand() {
        HandlerResponse r = HandlerResponseImpl.create(HandlerResponse.Response.STARTED);
        sender.defineAnswer(r);
        HandlerResponse response = cs.sendSequenceCommand(
                SequenceCommand.ABORT,
                Activity.START,
                completionListener
        );

        assertEquals(response, r);

        HandlerResponse r1 = HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);
        cu.updateOcs(Action.getCurrentId(), r1);

        synchronized (completionListener) {
            try {
                completionListener.wait(1000);
            } catch (InterruptedException e) {
                fail("Thread interrupted");
            }
        }
        assertTrue(completionListener.wasInvoked());
        assertEquals(r1, completionListener.getLastResponse());
    }

    /**
     * This case test a command whose associated handler finishes faster than
     * the code that receivesthe request... so we get "Completed" for instance,
     * for an action we have not quite yet send the "Started".
     */
    @Test
    public void testFasterHandler() {

        //the answer to the command will be Started
        HandlerResponse r = HandlerResponseImpl.create(HandlerResponse.Response.STARTED);
        sender.defineAnswer(r);

        //let's configure the ActionSender to reply completion information

        //we will simulate the situation when a fast handler returns first...
        HandlerResponse r1 = HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);

        //configure the executor in the command sender to send completion
        //information BEFORE answering the command.
        executor.simulateFastHandler(r1);

        //This will send the command, which normally would have
        //returned "STARTED". However, since we receive first a "COMPLETED"
        //due to the handler, internally the command sender has to
        //consider that and return the final answer.
        HandlerResponse response = cs.sendSequenceCommand(
                SequenceCommand.DATUM,
                Activity.START,
                completionListener
        );

        //so the answer should be the final one. 
        assertEquals(r1, response);
    }


}

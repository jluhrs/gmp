package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.gmp.commands.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionManager;
import edu.gemini.aspen.gmp.commands.test.TestActionSender;
import edu.gemini.aspen.gmp.commands.test.TestRebootManager;
import edu.gemini.aspen.gmp.commands.impl.CommandUpdaterImpl;
import edu.gemini.aspen.gmp.commands.messaging.JmsActionMessageBuilder;
import edu.gemini.aspen.gmp.util.commands.HandlerResponseImpl;

/**
 * Test class for the REBOOT sender executor
 */
public class RebootSenderExecutorTest {


    DefaultConfiguration configGMP;
    DefaultConfiguration configReboot;
    DefaultConfiguration configNone;

    RebootSenderExecutor executor;

    TestActionSender sender;
    final TestRebootManager rebootManager = new TestRebootManager();

    //A completion listener for the reboot sequence command
    final CompletionListener rebootCompletionListener = new CompletionListener() {

        public boolean invoked = false;

        public void onHandlerResponse(HandlerResponse response,
                                      SequenceCommand command,
                                      Activity activity, Configuration config) {
            //just notifies that it was invoked.
            invoked = true;
            synchronized (this) {
                notifyAll();
            }
        }
    };

    @Before
    public void setUp() {

        configGMP = new DefaultConfiguration();
        configGMP.put(new ConfigPath("REBOOT_OPT"), "GMP");

        configReboot = new DefaultConfiguration();
        configReboot.put(new ConfigPath("REBOOT_OPT"), "REBOOT");

        configNone = new DefaultConfiguration();
        configNone.put(new ConfigPath("REBOOT_OPT"), "NONE");

        executor = new RebootSenderExecutor(new JmsActionMessageBuilder(), rebootManager);

        sender = new TestActionSender();

        rebootManager.reset();

    }

    @Test
    public void testRebootPreset() {

        Action action = new Action(SequenceCommand.REBOOT,
                Activity.PRESET, configGMP, null);


        //set the sender to anything but ACCEPTED
        sender.defineAnswer(HandlerResponseImpl.createError("error"));

        HandlerResponse response = executor.execute(action, sender);

        //and make sure the response is ACCEPTED, regardless
        assertEquals(HandlerResponseImpl.create(HandlerResponse.Response.ACCEPTED), response);

    }


    @Test
    public void testRebootCancel() {

        Action action = new Action(SequenceCommand.REBOOT,
                Activity.CANCEL, configGMP, null);


        //set the sender to anything but ERROR
        sender.defineAnswer(HandlerResponseImpl.create(HandlerResponse.Response.ACCEPTED));

        HandlerResponse response = executor.execute(action, sender);

        //and make sure the response is the right ERROR message, regardless

        assertEquals(HandlerResponseImpl.createError("Can't cancel a REBOOT sequence command"), response);

    }


    @Test
    public void testRebootWithoutConfig() {
        Action action = new Action(SequenceCommand.REBOOT,
                Activity.START, null, null);

        //if null the answer should be the one of the sender, i.e,
        //it's like a NONE argument

        HandlerResponse[] responses = new HandlerResponse[]{
                HandlerResponseImpl.create(HandlerResponse.Response.ACCEPTED),
                HandlerResponseImpl.create(HandlerResponse.Response.STARTED),
                HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED),
                HandlerResponseImpl.createError("error")
        };

        for (HandlerResponse response : responses) {
            rebootManager.reset();
            sender.defineAnswer(response);
            HandlerResponse answer = executor.execute(action, sender);
            //wait for the reboot manager to be invoked, if the answer was completed.
            if (response.getResponse() == HandlerResponse.Response.COMPLETED) {
                synchronized (rebootManager) {
                    try {
                        rebootManager.wait(5000);
                    } catch (InterruptedException e) {
                        fail("Reboot Manager interrupted");
                    }
                }
                //make sure the reboot manager got the right arg
                assertEquals(RebootArgument.NONE, rebootManager.getReceivedArgument());
            }
            //verify the answer is the one defined by the sender. 
            assertEquals(response, answer);
        }
    }

    @Test
    public void testRebootWithInvalidConfigs() {

        DefaultConfiguration c = new DefaultConfiguration();
        c.put(new ConfigPath("REBOOT_OPT"), "Invalid");

        Action action = new Action(SequenceCommand.REBOOT,
                Activity.START, c, null);

        sender.defineAnswer(HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));

        HandlerResponse response = executor.execute(action, sender);

        assertEquals(HandlerResponseImpl.createError("Invalid argument for the REBOOT sequence command: " + c), response);


        c = new DefaultConfiguration();
        c.put(new ConfigPath("INVALID_KEY"), "REBOOT");
        action = new Action(SequenceCommand.REBOOT,
                Activity.START, c, null);
        response = executor.execute(action, sender);

        assertEquals(HandlerResponseImpl.createError("Invalid argument for the REBOOT sequence command: " + c), response);

    }


    @Test
    public void testRebootWithNoneArg() {
        testRebootWithImmediateCompletion(configNone, RebootArgument.NONE);
        testRebootWithLaterCompletion(configNone, RebootArgument.NONE);
    }

    @Test
    public void testRebootWithGMPArg() {
        testRebootWithImmediateCompletion(configGMP, RebootArgument.GMP);
        testRebootWithLaterCompletion(configGMP, RebootArgument.GMP);
    }

    @Test
    public void testRebootWithRebootArg() {
        testRebootWithImmediateCompletion(configReboot, RebootArgument.REBOOT);
        testRebootWithLaterCompletion(configReboot, RebootArgument.REBOOT);
    }


    //auxiliary method to exercise the reboot executor with different configurations
    //that will perform the reboot, asumming the caller completes immediately.
    private void testRebootWithImmediateCompletion(Configuration config, RebootArgument expectedArg) {
        //activities that produce an execution of the reboot sequence command.
        Activity activities[] = new Activity[]{
                Activity.START,
                Activity.PRESET_START
        };

        for (Activity activity : activities) {
            Action action = new Action(SequenceCommand.REBOOT,
                    activity, config, null);
            //if the sender returns COMPLETED, this means the instrument
            //will execute the reboot with the supplied argument.

            sender.defineAnswer(HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED));
            HandlerResponse response = executor.execute(action, sender);

            synchronized (rebootManager) {
                try {
                    rebootManager.wait(5000);
                } catch (InterruptedException e) {
                    fail("Reboot Manager interrupted");
                }
            }
            //make sure the reboot manager got the right arg
            assertEquals(expectedArg, rebootManager.getReceivedArgument());
            //and verify the response is also COMPLETED
            assertEquals(HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED), response);
        }
    }

    //auxiliary method to test the reboot with a later completion from the
    //PARK command sent to the instrument
    private void testRebootWithLaterCompletion(Configuration config, RebootArgument expectedArg) {

        ActionManager actionManager = new ActionManager();
        actionManager.start();

        CommandUpdaterImpl cu = new CommandUpdaterImpl(actionManager);

        Activity activities[] = new Activity[]{
                Activity.START,
                Activity.PRESET_START
        };

        for (Activity activity : activities) {
            Action action = new Action(SequenceCommand.REBOOT,
                    activity, config, rebootCompletionListener);


            actionManager.registerAction(action);

            sender.defineAnswer(HandlerResponseImpl.create(HandlerResponse.Response.STARTED));

            HandlerResponse response = executor.execute(action, sender);

            assertEquals(HandlerResponseImpl.create(HandlerResponse.Response.STARTED), response);


            //simulate a completion listener from the PARK sequene command...
            HandlerResponse r1 = HandlerResponseImpl.create(HandlerResponse.Response.COMPLETED);
            cu.updateOcs(Action.getCurrentId(), r1);

            //now, we wait for the reboot completion listener to be invoked...

            synchronized (rebootCompletionListener) {
                try {
                    rebootCompletionListener.wait(5000);
                } catch (InterruptedException e) {
                    fail("Reboot Completion Listener interrupted");
                }
            }

            //if everything went fine, the reboot manager should have been called
            //make sure the reboot manager got the right arg
            assertEquals(expectedArg, rebootManager.getReceivedArgument());
        }
    }
}


package edu.gemini.aspen.gmp.commands.model.executors;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionSender;
import edu.gemini.aspen.gmp.commands.test.ActionSenderMock;
import edu.gemini.aspen.gmp.commands.test.RebootManagerMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.configuration;
import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test class for the REBOOT sender executor
 */
public class RebootSenderExecutorTest {
    private Configuration configGMP;
    private Configuration configReboot;
    private Configuration configNone;

    private RebootSenderExecutor executor;

    private final RebootManagerMock rebootManager = new RebootManagerMock();

    private final CompletionListenerMock rebootCompletionListener = new CompletionListenerMock();

    @Before
    public void setUp() {
        configGMP = configuration(configPath("REBOOT_OPT"), "GMP");
        configReboot = configuration(configPath("REBOOT_OPT"), "REBOOT");
        configNone = configuration(configPath("REBOOT_OPT"), "NONE");

        executor = new RebootSenderExecutor(rebootManager);

        rebootManager.reset();
        rebootCompletionListener.reset();
    }

    @Test
    public void testRebootWithPreset() {
        Action action = new Action(new Command(SequenceCommand.REBOOT,
                Activity.PRESET, configGMP), new CompletionListenerMock());

        //set the sender to anything but ACCEPTED
        ActionSender sender = new ActionSenderMock(HandlerResponse.createError("error"));

        HandlerResponse response = executor.execute(action, sender);

        //and make sure the response is ACCEPTED, regardless
        assertEquals(HandlerResponse.ACCEPTED, response);
    }

    @Test
    public void testRebootWithCancel() {
        Action action = new Action(new Command(SequenceCommand.REBOOT,
                Activity.CANCEL, configGMP), new CompletionListenerMock());

        //set the sender to anything but ERROR
        ActionSender sender = new ActionSenderMock(HandlerResponse.ACCEPTED);

        HandlerResponse response = executor.execute(action, sender);

        //and make sure the response is the right ERROR message, regardless
        assertEquals(HandlerResponse.createError("Can't cancel a REBOOT sequence command"), response);
    }

    @Ignore//Reboot commands can no longer be created without a config
    @Test
    public void testRebootWithoutConfig() {
        Action action = new Action(new Command(SequenceCommand.REBOOT,
                Activity.START, emptyConfiguration()), new CompletionListenerMock());

        //if null the answer should be the one of the sender, i.e,
        //it's like a NONE argument

        HandlerResponse[] responses = new HandlerResponse[]{
                HandlerResponse.ACCEPTED,
                HandlerResponse.STARTED,
                HandlerResponse.COMPLETED,
                HandlerResponse.createError("error")
        };

        for (HandlerResponse response : responses) {
            rebootManager.reset();
            ActionSender sender = new ActionSenderMock(response);
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
            //verify the answer is always COMPLETED, despite of the sender
            assertEquals(HandlerResponse.COMPLETED, answer);
        }
    }

    @Ignore//Reboot commands can no longer be created with invalid config
    @Test
    public void testRebootWithInvalidConfigs() {
        Configuration c = configuration(configPath("REBOOT_OPT"), "Invalid");

        Action action = new Action(new Command(SequenceCommand.REBOOT,
                Activity.START, c), new CompletionListenerMock());

        ActionSender sender = new ActionSenderMock(HandlerResponse.COMPLETED);

        HandlerResponse response = executor.execute(action, sender);

        assertEquals(HandlerResponse.createError("Invalid argument for the REBOOT sequence command: " + c), response);


        c = configuration(configPath("INVALID_KEY"), "REBOOT");
        action = new Action(new Command(SequenceCommand.REBOOT,
                Activity.START, c), new CompletionListenerMock());
        response = executor.execute(action, sender);

        assertEquals(HandlerResponse.createError("Invalid argument for the REBOOT sequence command: " + c), response);
    }

    @Test
    public void testRebootWithNoneArg() {
        testRebootWithImmediateCompletion(configNone, RebootArgument.NONE);
    }

    @Test
    public void testRebootWithGMPArg() {
        testRebootWithImmediateCompletion(configGMP, RebootArgument.GMP);
    }

    @Test
    public void testRebootWithRebootArg() {
        testRebootWithImmediateCompletion(configReboot, RebootArgument.REBOOT);
    }

    //auxiliary method to exercise the reboot executor with different configurations
    //that will perform the reboot, assuming the caller completes immediately.
    private void testRebootWithImmediateCompletion(Configuration config, RebootArgument expectedArg) {
        //activities that produce an execution of the reboot sequence command.
        Activity activities[] = new Activity[]{
                Activity.START,
                Activity.PRESET_START
        };

        for (Activity activity : activities) {
            Action action = new Action(new Command(SequenceCommand.REBOOT,
                    activity, config), new CompletionListenerMock());
            //if the sender returns COMPLETED, this means the instrument
            //will execute the reboot with the supplied argument.

            ActionSender sender = new ActionSenderMock(HandlerResponse.COMPLETED);
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
            assertEquals(HandlerResponse.COMPLETED, response);
        }
    }
}


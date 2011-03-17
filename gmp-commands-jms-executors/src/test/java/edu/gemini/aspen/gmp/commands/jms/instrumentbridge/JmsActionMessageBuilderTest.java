package edu.gemini.aspen.gmp.commands.jms.instrumentbridge;

import com.gargoylesoftware.base.testing.EqualsTester;
import edu.gemini.aspen.giapi.commands.Activity;
import edu.gemini.aspen.giapi.commands.Command;
import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.commands.SequenceCommand;
import edu.gemini.aspen.giapitestsupport.commands.CompletionListenerMock;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import org.junit.Test;

/**
 * Test case for the Action Message builder based on JMS
 */
public class JmsActionMessageBuilderTest extends ActionMessageBuilderTestBase {

    protected ActionMessageBuilder getActionMessageBuilder() {
        return new JmsActionMessageBuilder();
    }

    @Test
    public void testActionMessageEquality() {
        JmsActionMessageBuilder messageBuilder = new JmsActionMessageBuilder();
        ActionMessage a = messageBuilder.buildActionMessage(action);
        ActionMessage b = messageBuilder.buildActionMessage(action);
        ActionMessage c = messageBuilder.buildActionMessage(action, ConfigPath.configPath("Y"));
        ActionMessage d = null;

        new EqualsTester(a, b, c, d);

        action = new Action(new Command(SequenceCommand.APPLY, Activity.START, config), new CompletionListenerMock());
        c = messageBuilder.buildActionMessage(action);

        new EqualsTester(a, b, c, d);

        action = new Action(new Command(SequenceCommand.ABORT, Activity.PRESET, config), new CompletionListenerMock());
        c = messageBuilder.buildActionMessage(action);

        new EqualsTester(a, b, c, d);
    }
}

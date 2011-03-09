package edu.gemini.aspen.gmp.commands.messaging;

import com.gargoylesoftware.base.testing.EqualsTester;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilderTestBase;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;

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
        ActionMessage c = messageBuilder.buildActionMessage(action, configPath("Y"));
        ActionMessage d = null;

        new EqualsTester(a, b, c, d);
    }
}

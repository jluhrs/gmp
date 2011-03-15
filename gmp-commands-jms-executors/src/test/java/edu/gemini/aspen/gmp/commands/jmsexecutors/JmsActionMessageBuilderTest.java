package edu.gemini.aspen.gmp.commands.jmsexecutors;

import com.gargoylesoftware.base.testing.EqualsTester;
import edu.gemini.aspen.giapi.commands.ConfigPath;
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
    }
}

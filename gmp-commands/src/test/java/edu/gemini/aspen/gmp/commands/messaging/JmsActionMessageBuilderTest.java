package edu.gemini.aspen.gmp.commands.messaging;

import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilderTestBase;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

/**
 * Test case for the Action Message builder based on JMS
 */
public class JmsActionMessageBuilderTest extends ActionMessageBuilderTestBase {

    protected ActionMessageBuilder getActionMessageBuilder() {
        return new JmsActionMessageBuilder();
    }
}

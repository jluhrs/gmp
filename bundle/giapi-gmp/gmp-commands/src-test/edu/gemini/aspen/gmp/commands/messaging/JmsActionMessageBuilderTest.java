package edu.gemini.aspen.gmp.commands.messaging;

import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilderTestBase;
import edu.gemini.aspen.gmp.commands.model.ActionMessageBuilder;

/**
 * Test case for the Action Message builder based on JMS
 */
public class JmsActionMessageBuilderTest extends ActionMessageBuilderTestBase {

    private JmsActionMessageBuilder _builder;

    protected ActionMessageBuilder getActionMessageBuilder() {
        if (_builder == null) {
            _builder = new JmsActionMessageBuilder();
        }
        return _builder;
    }
}

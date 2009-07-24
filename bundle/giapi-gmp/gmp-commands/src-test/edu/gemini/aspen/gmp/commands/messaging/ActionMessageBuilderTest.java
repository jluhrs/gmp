package edu.gemini.aspen.gmp.commands.messaging;

import org.junit.Before;
import edu.gemini.aspen.gmp.commands.model.messaging.ActionMessageBuilder;
import edu.gemini.aspen.gmp.commands.model.Action;
import edu.gemini.aspen.gmp.commands.model.ActionMessage;
import edu.gemini.aspen.gmp.commands.model.ActionMessageTestBase;

/**
 * Test class for the ActionMessageBuilder
 */
public class ActionMessageBuilderTest extends ActionMessageTestBase {


    private ActionMessageBuilder _builder;


    @Before
    public void initMessageBuilder() {
        _builder = new ActionMessageBuilder();
    }

    protected ActionMessage getActionMessage(Action a) {
        return _builder.buildActionMessage(a);
    }
}

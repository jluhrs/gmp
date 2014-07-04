package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.DefaultConfiguration.emptyConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CompletionInformationTest {
    private HandlerResponse handlerResponse;
    private SequenceCommand init;
    private Activity start;
    private Configuration configuration;
    private Command command;

    @Before
    public void setUp() throws Exception {
        handlerResponse = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        init = SequenceCommand.INIT;
        start = Activity.START;
        configuration = emptyConfiguration();
        command = new Command(init, start, configuration);
    }

    @Test
    public void testConstructor() {
        CompletionInformation completionInformation = new CompletionInformation(handlerResponse, command);
        assertNotNull(completionInformation);
        assertEquals(handlerResponse, completionInformation.getHandlerResponse());
        assertEquals(command, completionInformation.getCommand());
        assertEquals("[[response=[ACCEPTED]][command=INIT][activity=START][]]", completionInformation.toString());
    }

    @Test
    public void testEquality() {
        CompletionInformation a = new CompletionInformation(handlerResponse, command);
        CompletionInformation b = new CompletionInformation(handlerResponse, command);
        CompletionInformation c = new CompletionInformation(handlerResponse, new Command(init, Activity.PRESET, configuration));
        CompletionInformation d = new CompletionInformation(handlerResponse, new Command(init, Activity.PRESET, configuration)) {
        };

        new EqualsTester(a, b, c, d);
    }

}

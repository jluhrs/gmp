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

    @Before
    public void setUp() throws Exception {
        handlerResponse = HandlerResponse.get(HandlerResponse.Response.ACCEPTED);
        init = SequenceCommand.INIT;
        start = Activity.START;
        configuration = emptyConfiguration();
    }

    @Test
    public void testConstructor() {
        CompletionInformation completionInformation = new CompletionInformation(handlerResponse, init, start, configuration);
        assertNotNull(completionInformation);
        assertEquals(handlerResponse, completionInformation.getHandlerResponse());
        assertEquals(init, completionInformation.getSequenceCommand());
        assertEquals(start, completionInformation.getActivity());
        assertEquals(configuration, completionInformation.getConfiguration());
        assertEquals("[[response=[ACCEPTED]][command=INIT][activity=START][{config={}}]]", completionInformation.toString());
    }

    @Test
    public void testEquality() {
        CompletionInformation a = new CompletionInformation(handlerResponse, init, start, configuration);
        CompletionInformation b = new CompletionInformation(handlerResponse, init, start, configuration);
        CompletionInformation c = new CompletionInformation(handlerResponse, init, Activity.PRESET, configuration);
        CompletionInformation d = new CompletionInformation(handlerResponse, init, Activity.PRESET, configuration) {};

        new EqualsTester(a, b, c, d);
    }
}

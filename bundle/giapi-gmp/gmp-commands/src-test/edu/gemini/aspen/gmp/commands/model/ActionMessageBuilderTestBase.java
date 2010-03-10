package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.gmp.commands.*;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.*;

/**
 * Test class for the ActionMessageBuilder
 */
public abstract class ActionMessageBuilderTestBase extends ActionMessageTestBase {



    protected abstract ActionMessageBuilder getActionMessageBuilder();

    /**
     * Test the action messages produced by this builder
     *
     * @param a Action to be converted into a message
     * @return an action Message produced by this builder
     */
    protected ActionMessage getActionMessage(Action a) {
        return getActionMessageBuilder().buildActionMessage(a);
    }

    /**
     * Test the message building when specifiying sub configurations
     * to match
     */
    @Test
    public void testBuildMessageWithConfigPath() {
        TreeMap<ConfigPath, String> configuration = new TreeMap<ConfigPath, String>();

        configuration.put(new ConfigPath("X.val1"), "x1");
        configuration.put(new ConfigPath("X.val2"), "x2");
        configuration.put(new ConfigPath("X.val3"), "x3");

        configuration.put(new ConfigPath("X:A.val1"), "xa1");
        configuration.put(new ConfigPath("X:A.val2"), "xa2");
        configuration.put(new ConfigPath("X:A.val3"), "xa3");

        configuration.put(new ConfigPath("X:B.val1"), "xb1");
        configuration.put(new ConfigPath("X:B.val2"), "xb2");
        configuration.put(new ConfigPath("X:B.val3"), "xb3");

        configuration.put(new ConfigPath("X:C.val1"), "xc1");
        configuration.put(new ConfigPath("X:C.val2"), "xc2");
        configuration.put(new ConfigPath("X:C.val3"), "xc3");

        Configuration config = new DefaultConfiguration(configuration);

        Action action = new Action(SequenceCommand.ABORT, Activity.START, config, null);

        List<ConfigPath> configPaths = new ArrayList<ConfigPath>();

        configPaths.add(new ConfigPath("X:A"));
        configPaths.add(new ConfigPath("X:B"));
        configPaths.add(new ConfigPath("X:C"));

        testConfigPaths(action, configuration, configPaths, 3);

        testConfigPaths(action, configuration, Collections.singletonList(new ConfigPath("X")), 12);

        //finally, test with a null Config Path.
        ActionMessage am = getActionMessageBuilder().buildActionMessage(action, null);
        Map<String, Object> data = am.getDataElements();
        //null should be interpreted as no-filter, so all the stuff should be there
        assertEquals(12, data.keySet().size());
        for (String keys : data.keySet()) {
            ConfigPath conf = new ConfigPath(keys);
            String value = configuration.get(conf);
            assertEquals(value, data.get(keys));
        }


    }


    //just an auxiliar method to test config paths from a list
    private void testConfigPaths(Action a,
                                 Map<ConfigPath, String> configuration,
                                 List<ConfigPath> configPaths,
                                 int expectedMatches) {
        for (ConfigPath cp : configPaths) {
            ActionMessage am = getActionMessageBuilder().buildActionMessage(a, cp);
            Map<String, Object> data = am.getDataElements();
            assertEquals(expectedMatches, data.keySet().size());
            for (String keys : data.keySet()) {
                ConfigPath conf = new ConfigPath(keys);
                String value = configuration.get(conf);
                assertEquals(value, data.get(keys));
            }
        }
    }


}

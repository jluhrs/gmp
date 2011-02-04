package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.giapi.util.jms.JmsKeys;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static org.junit.Assert.assertEquals;

/**
 *  Base test class for the ActionMessage interface
 */
public abstract class ActionMessageTestBase {
    /**
     * Get the action message that will be tested
     * @param a Action to be converted into a message
     * @return action message concrete implementation
     */
    protected abstract ActionMessage getActionMessage(Action a);

    private List<Action> _action;

    @Before
    public void setUp() {
        _action = new ArrayList<Action>();

        Configuration dummyConfig = DefaultConfiguration.emptyConfiguration();

        for (SequenceCommand sc: SequenceCommand.values()) {
            for (Activity activity: Activity.values()) {
                _action.add(new Action(sc, activity, dummyConfig, null));
            }
        }
    }

    @Test
    public void testDestinationData() {
        for (Action a: _action) {
            ActionMessage am = getActionMessage(a);
            DestinationData dd = am.getDestinationData();

            //type must be topic
            assertEquals(DestinationType.TOPIC, dd.getType());

            //name
            StringBuilder sb = new StringBuilder(JmsKeys.GMP_SEQUENCE_COMMAND_PREFIX);
            sb.append(a.getSequenceCommand().getName());
            assertEquals(sb.toString(), dd.getName());
        }
    }

    @Test
    public void testActionMessageProperties() {

        for (Action a: _action) {
            ActionMessage am = getActionMessage(a);
            //action id is a property
            int id = a.getId();
            Map<String, Object> props = am.getProperties();

            assertEquals(id, props.get(JmsKeys.GMP_ACTIONID_PROP));

            //activity is also a property
            String act = (String) props.get(JmsKeys.GMP_ACTIVITY_PROP);
            assertEquals(a.getActivity(), Activity.toActivity(act));
        }
    }

    @Test
    public void testContent() {
        TreeMap<ConfigPath, String> map = new TreeMap<ConfigPath, String>();

        map.put(configPath("X.val"), "x");
        map.put(configPath("Y.val"), "y");
        map.put(configPath("Z.val"), "z");

        map.put(configPath("X:A.val"), "xa");
        map.put(configPath("X:B.val"), "xb");
        map.put(configPath("X:C.val"), "xc");

        map.put(configPath("Y:A.val"), "ya");
        map.put(configPath("Y:B.val"), "yb");
        map.put(configPath("Y:C.val"), "yc");

        map.put(configPath("Z:A.val"), "za");
        map.put(configPath("Z:B.val"), "zb");
        map.put(configPath("Z:C.val"), "zc");

        Configuration config = new DefaultConfiguration(map);

        Action a = new Action(SequenceCommand.ABORT, Activity.START, config, null);

        ActionMessage am = getActionMessage(a);

        Map<String, Object> dataMap = am.getDataElements();

        for(ConfigPath path : map.keySet()) {

            String value = map.get(path);
            String valueInMap = (String)dataMap.get(path.getName());

            assertEquals(value,  valueInMap);
        }
    }
}

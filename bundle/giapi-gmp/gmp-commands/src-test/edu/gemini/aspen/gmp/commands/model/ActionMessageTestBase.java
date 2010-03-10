package edu.gemini.aspen.gmp.commands.model;

import edu.gemini.aspen.gmp.commands.*;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

import java.util.*;

import edu.gemini.aspen.gmp.util.jms.GmpKeys;
import edu.gemini.jms.api.DestinationData;
import edu.gemini.jms.api.DestinationType;

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

        Configuration dummyConfig = new DefaultConfiguration();

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
            StringBuilder sb = new StringBuilder(GmpKeys.GMP_SEQUENCE_COMMAND_PREFIX);
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

            assertEquals(id, props.get(GmpKeys.GMP_ACTIONID_PROP));

            //activity is also a property
            String act = (String) props.get(GmpKeys.GMP_ACTIVITY_PROP); 
            assertEquals(a.getActivity(), Activity.toActivity(act));
        }
    }

    @Test
    public void testContent() {

        TreeMap<ConfigPath, String> map = new TreeMap<ConfigPath, String>();

        map.put(new ConfigPath("X.val"), "x");
        map.put(new ConfigPath("Y.val"), "y");
        map.put(new ConfigPath("Z.val"), "z");

        map.put(new ConfigPath("X:A.val"), "xa");
        map.put(new ConfigPath("X:B.val"), "xb");
        map.put(new ConfigPath("X:C.val"), "xc");

        map.put(new ConfigPath("Y:A.val"), "ya");
        map.put(new ConfigPath("Y:B.val"), "yb");
        map.put(new ConfigPath("Y:C.val"), "yc");

        map.put(new ConfigPath("Z:A.val"), "za");
        map.put(new ConfigPath("Z:B.val"), "zb");
        map.put(new ConfigPath("Z:C.val"), "zc");



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

package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.logging.Logger;

public class StatusDispatcherTest {
    private static final Logger LOG = Logger.getLogger(StatusDispatcherTest.class.getName());
    StatusDispatcher dispatcher;
    int counter;
    @Before
    public void setUp() throws Exception {
        dispatcher=new StatusDispatcher();
        counter=0;
    }
    private abstract class TestHandler implements FilteredStatusHandler{

            @Override
            public String getName() {
                return "Filter: "+getFilter().toString();
            }

            @Override
            public void update(StatusItem item) {
                //check that we only get items that are children of our filter
                assertTrue(item.getName().startsWith(getFilter().toString()));
                counter++;
            }
    }
    @Test
    public void testUpdate() throws Exception {
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:a:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        });
        FilteredStatusHandler h = new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        };
        dispatcher.bindStatusHandler(h);
        dispatcher.unbindStatusHandler(h);
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b:2");
            }
        });
        dispatcher.update(new BasicStatus<String>("gpi:b:1","gpi:b:1"));
        //check that the right amount of handlers were called
        assertEquals(4, counter);
    }

}

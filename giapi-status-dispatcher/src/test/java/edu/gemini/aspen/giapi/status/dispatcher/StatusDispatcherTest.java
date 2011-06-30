package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregateImpl;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.aspen.giapi.util.jms.status.StatusSetter;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatusDispatcherTest {

    private StatusDispatcher dispatcher;
    private AtomicInteger counter;
    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        dispatcher = new StatusDispatcher();
        counter = new AtomicInteger(0);
        latch = new CountDownLatch(4);

        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:a:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:b");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:b");
            }
        });
        FilteredStatusHandler h = new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:b");
            }
        };
        dispatcher.bindStatusHandler(h);
        dispatcher.unbindStatusHandler(h);
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:b:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:b:2");
            }
        });
    }

    private abstract class TestHandler implements FilteredStatusHandler {

        @Override
        public String getName() {
            return "Filter: " + getFilter().toString();
        }

        @Override
        public <T> void update(StatusItem<T> item) {
            //check that we only get items that are children of our filter
            assertTrue(item.getName().startsWith(getFilter().toString()));
            counter.incrementAndGet();
            latch.countDown();
        }
    }

    @Test
    public void testUpdateLocal2HandlersPerFilter() throws Exception {
        //add a second handler for "gpi:", the first was added in setUp()
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:");
            }
        });

        latch = new CountDownLatch(2);

        dispatcher.update(new BasicStatus<String>("gpi:", "any value"));
        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(2, counter.get());
    }

    @Test
    public void testUpdateLocal() throws Exception {
        dispatcher.update(new BasicStatus<String>("gpi:b:1", "gpi:b:1"));
        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(4, counter.get());
    }

    @Test
    public void testUpdateViaJms() throws Exception {
        //create jms provider
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider("vm://StatusDispatcherTest");
        provider.startConnection();

        //create status service connected to the jms provider
        StatusHandlerAggregate agg = new StatusHandlerAggregateImpl();
        StatusService statusservice = new StatusService(agg, "Status Service", ">", provider);
        statusservice.initialize();

        //resgister the status dispatcher as a status handler
        agg.bindStatusHandler(dispatcher);


        StatusSetter ss = new StatusSetter("gpi:b:1");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<String>("gpi:b:1", "gpi:b:1"));

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(4, counter.get());

        ss.stopJms();
    }
}

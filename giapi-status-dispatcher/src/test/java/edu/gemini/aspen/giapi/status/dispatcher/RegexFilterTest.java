package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.filters.RegexFilter;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RegexFilterTest {

    private StatusDispatcher dispatcher;
    private AtomicInteger counter;
    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        dispatcher = new StatusDispatcher();
        counter = new AtomicInteger(0);
        latch = null;

        //lets add some handlers:
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public RegexFilter getFilter() {
                return new RegexFilter("gpi.*");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public RegexFilter getFilter() {
                return new RegexFilter("gpi:a:test\\.1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public RegexFilter getFilter() {
                return new RegexFilter("bla", ".*");
            }
        });

        //just add and remove one to see if that works.
        FilteredStatusHandler h = new TestHandler() {
            @Override
            public RegexFilter getFilter() {
                return new RegexFilter("");
            }
        };
        dispatcher.bindStatusHandler(h);
        dispatcher.unbindStatusHandler(h);


    }

    private abstract class TestHandler implements FilteredStatusHandler {

        @Override
        public String getName() {
            return "Handler using filter: " + getFilter().toString();
        }

        @Override
        public String toString() {
            return getName();
        }

        @Override
        public <T> void update(StatusItem<T> item) {
            //check that we only get items that match this filter
            assertTrue("Got item: " + item.getName() + " for filter " + getFilter(), getFilter().match(item));

            counter.incrementAndGet();
            latch.countDown();
        }
    }

    @Test
    public void testUpdateLocal2() throws Exception {
        latch = new CountDownLatch(8);

        dispatcher.update(new BasicStatus<String>("gpi:a", "any value")); //will match twice
        dispatcher.update(new BasicStatus<String>("gpi:a:test.1", "any value")); //will match three times
        dispatcher.update(new BasicStatus<String>("agpi:b", "any value")); //will match once
        dispatcher.update(new BasicStatus<String>("gpi:a:test.2", "any value")); //will match twice
        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(8, counter.get());
    }

    @Test
    public void testUpdateLocal() throws Exception {
        latch = new CountDownLatch(3);

        dispatcher.update(new BasicStatus<String>("gpi:a:test.1", "test value"));

        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(3, counter.get());
    }

    @Test
    public void testUpdateViaJms() throws Exception {
        latch = new CountDownLatch(2);

        //create jms provider
        ActiveMQJmsProvider provider = new ActiveMQJmsProvider("vm://ConfigPathFilterTest?broker.persistent=false");
        provider.startConnection();

        //create status service connected to the jms provider
        StatusHandlerAggregate agg = new StatusHandlerAggregate();
        StatusService statusservice = new StatusService(agg, "Status Service", ">");
        statusservice.startJms(provider);

        //resgister the status dispatcher as a status handler
        agg.bindStatusHandler(dispatcher);


        StatusSetterImpl ss = new StatusSetterImpl("Test Status Setter", "gpi:a:test.2");
        ss.startJms(provider);
        ss.setStatusItem(new BasicStatus<String>("gpi:a:test.2", "test value"));

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(2, counter.get());

        ss.stopJms();
    }
}

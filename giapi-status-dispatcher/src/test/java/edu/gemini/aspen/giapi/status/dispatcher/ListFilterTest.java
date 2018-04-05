package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.filters.ListFilter;
import edu.gemini.aspen.giapi.status.dispatcher.filters.TimedListFilter;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.setter.StatusSetterImpl;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ListFilterTest {

    private StatusDispatcher dispatcher;
    private AtomicInteger counter;
    private CountDownLatch latch;

    @Before
    public void setUp() {
        dispatcher = new StatusDispatcher();
        counter = new AtomicInteger(0);
        latch = null;

        //lets add some handlers:
        //List ones: gpi:a:test.1, gpi:a:test.2
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ListFilter getFilter() {
                return new ListFilter("gpi:a:test.1", "gpi:a:test.2");
            }
        });
        //TimedList one: gpi:a:test.3
        dispatcher.bindStatusHandler(new TestHandler() {
            private TimedListFilter filter = new TimedListFilter(Duration.ofMillis(200),"gpi:a:test.3");
            @Override
            public TimedListFilter getFilter() {
                return filter;
            }
        });

        //just add and remove one to see if that works.
        FilteredStatusHandler h = new TestHandler() {
            @Override
            public ListFilter getFilter() {
                return new ListFilter("gpi:b.1");
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
            //assertTrue("Got item: "+item.getName() + " for filter " + getFilter(), getFilter().match(item));

            counter.incrementAndGet();
            latch.countDown();
        }
    }

    @Test
    public void testUpdateLocal2() throws Exception {
        latch = new CountDownLatch(1);

        dispatcher.update(new BasicStatus<>("gpi:a", "any value")); //will not match
        dispatcher.update(new BasicStatus<>("gpi:a:test.2", "any value")); //will match once
        dispatcher.update(new BasicStatus<>("gpi:b", "any value")); //will not match
        dispatcher.update(new BasicStatus<>("gpi:c", "any value"));//will not match
        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }

    @Test
    public void testUpdateLocal() throws Exception {
        latch = new CountDownLatch(1);

        dispatcher.update(new BasicStatus<>("gpi:a:test.1", "test value"));

        //check that the right amount of handlers were called
        latch.await(1, TimeUnit.SECONDS);
        assertEquals(1, counter.get());
    }


    @Test
    public void testTimeListFilterLocal() throws Exception {
        latch = new CountDownLatch(2);

        dispatcher.update(new BasicStatus<>("gpi:a:test.3", "test value"));
        dispatcher.update(new BasicStatus<>("gpi:a:test.3", "test value"));
        Thread.sleep(210);
        dispatcher.update(new BasicStatus<>("gpi:a:test.3", "test value"));

        //check that the right amount of handlers were called
        latch.await(2, TimeUnit.SECONDS);
        assertEquals(2, counter.get());
    }

    @Test
    public void testUpdateViaJms() throws Exception {
        latch = new CountDownLatch(1);

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
        ss.setStatusItem(new BasicStatus<>("gpi:a:test.2", "test value"));

        latch.await(1, TimeUnit.SECONDS);
        assertEquals(1, counter.get());

        ss.stopJms();
    }
}

package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler;
import edu.gemini.aspen.giapi.status.dispatcher.StatusItemFilter;
import edu.gemini.aspen.giapi.status.dispatcher.filters.ConfigPathFilter;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
* Class TestHandler
*
* @author Nicolas A. Barriga
*         Date: 2/24/11
*/
@Component
@Provides(specifications = FilteredStatusHandler.class)
public class TestHandler implements FilteredStatusHandler {
    private int counter = 0;
    private final CountDownLatch latch = new CountDownLatch(1);
    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());

    @Validate
    public void initialize(){
        LOG.info("Constructing TestHandler");
    }
    @Override
    public StatusItemFilter getFilter() {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + this + " ");
        return new ConfigPathFilter("gpi:status1");
    }

    @Override
    public String getName() {
        return "TestHandler";
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + this + " " + item);


        LOG.info(item.toString());
        counter++;
        latch.countDown();
    }

    public boolean waitOnLatch(long l, java.util.concurrent.TimeUnit timeUnit) throws java.lang.InterruptedException{
        return latch.await(l,timeUnit);
    }

    public int getCounter(){
        return counter;
    }
}
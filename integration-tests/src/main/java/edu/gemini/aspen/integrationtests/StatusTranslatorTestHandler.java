package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.FilteredStatusHandler;
import edu.gemini.aspen.giapi.status.dispatcher.filters.ConfigPathFilter;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
* Class TestHandler
*
* @author Nicolas A. Barriga
*         Date: 2/24/11
*/
public class StatusTranslatorTestHandler implements FilteredStatusHandler {
    private int counter = 0;
    private final CountDownLatch latch = new CountDownLatch(1);
    private static final Logger LOG = Logger.getLogger(StatusTranslatorTestHandler.class.getName());
    private Health value;

    @Override
    public ConfigPathFilter getFilter() {
        return new ConfigPathFilter("gpisim:new");
    }

    @Override
    public String getName() {
        return "StatusTranslatorTestHandler";
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        LOG.info("Received "+item);
        value=(Health)item.getValue();
        counter++;
        latch.countDown();
    }

    public boolean waitOnLatch(long l, java.util.concurrent.TimeUnit timeUnit) throws InterruptedException{
        return latch.await(l,timeUnit);
    }

    public int getCounter(){
        return counter;
    }
    public Health getValue(){
        return value;
    }
}
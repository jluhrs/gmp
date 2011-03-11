package edu.gemini.aspen.heartbeatdistributor;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
* Class TestConsumerComponent. This class should be moved to an Integration tests bundle.
*
* @author Nicolas A. Barriga
*         Date: 3/10/11
*/
@Component
@Provides
public class TestConsumerComponent implements IHeartbeatConsumer{
    private final Logger LOG = Logger.getLogger(TestConsumerComponent.class.getName());
    private final CountDownLatch latch;

    private long last=-1;
    private long count=0;

    public TestConsumerComponent(int latchSize){
        latch=new CountDownLatch(latchSize);
    }

    public long getLast(){
        return last;
    }

    public long getCount(){
        return count;
    }

    @Override
    public void beat(long beatNumber) {
        LOG.info("Heartbeat: "+beatNumber);
        last=beatNumber;
        count++;
        latch.countDown();
    }

    /**
     * Wait until the latch has been activated (by an arriving beat), or a timeout has elapsed.
     *
     * @param l timeout length
     * @param timeUnit unit of the timeout
     * @throws java.lang.InterruptedException
     */
    public void waitOnLatch(long l, java.util.concurrent.TimeUnit timeUnit) throws java.lang.InterruptedException{
         latch.await(l,timeUnit);
    }

}

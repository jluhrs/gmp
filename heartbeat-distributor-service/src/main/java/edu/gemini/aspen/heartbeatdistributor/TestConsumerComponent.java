package edu.gemini.aspen.heartbeatdistributor;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;

import java.util.logging.Logger;

/**
* Class TestConsumerComponent
*
* @author Nicolas A. Barriga
*         Date: 3/10/11
*/
@Component
@Provides
public class TestConsumerComponent implements IHeartbeatConsumer{
    private final Logger LOG = Logger.getLogger(TestConsumerComponent.class.getName());
    private long last=0;
    public long getLast(){
        return last;
    }
    @Override
    public void beat(long beatNumber) {
        LOG.info("Heartbeat: "+beatNumber);
        last=beatNumber;
    }

    @Override
    public String getName() {
        return "TestConsumerComponent";
    }
}

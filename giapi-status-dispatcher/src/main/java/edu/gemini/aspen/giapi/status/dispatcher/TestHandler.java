package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.status.StatusItem;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Validate;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

/**
* Class TestHandler just for testing purposes.
* It is here because otherwise it is not part of the bundle and cannot be used for
* integration tests. In the future it should be moved to a specific bundle for integration tests.
*
* @author Nicolas A. Barriga
*         Date: 2/24/11
*/
@Component
@Provides(specifications = FilteredStatusHandler.class)
public class TestHandler implements FilteredStatusHandler {
    public int counter = 0;
    public CountDownLatch latch = new CountDownLatch(1);
    private static final Logger LOG = Logger.getLogger(TestHandler.class.getName());

    @Validate
    public void initialize(){
        LOG.info("Constructing TestHandler");
    }
    @Override
    public ConfigPath getFilter() {
        return new ConfigPath("gpi:status1");
    }

    @Override
    public String getName() {
        return "TestHandler";
    }

    @Override
    public void update(StatusItem item) {
        LOG.info(item.toString());
        counter++;
        latch.countDown();
    }
}
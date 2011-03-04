package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;
import org.apache.felix.ipojo.annotations.*;

import java.util.logging.Logger;

/**
* Class ExampleHandler2
*
* @author Nicolas A. Barriga
*         Date: 2/24/11
*/
@Component
@Instantiate
public class ExampleHandler2 {
    private static final Logger LOG = Logger.getLogger(ExampleHandler2.class.getName());
    @Requires
    private StatusDispatcher _dispatcher;
    public ExampleHandler2(){
        LOG.info("Constructing ExampleHandler2");

    }
    @Validate
    public void initialize() {
        LOG.info("Validating ExampleHandler2");
        _dispatcher.bindStatusHandler(new FilteredStatusHandler() {
            @Override
            public StatusItemFilter getFilter() {
                return new StatusItemFilter("gpi:status2");
            }

            @Override
            public String getName() {
                return "anonymous ExampleHandler2";
            }

            @Override
            public <T> void update(StatusItem<T> item) {
                LOG.info(item.toString());
            }
        });
    }

}

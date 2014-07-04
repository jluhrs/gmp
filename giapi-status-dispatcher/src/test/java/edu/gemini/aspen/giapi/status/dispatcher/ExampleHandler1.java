package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.dispatcher.filters.ConfigPathFilter;

import java.util.logging.Logger;

/**
* Class ExampleHandler1
*
* @author Nicolas A. Barriga
*         Date: 2/24/11
*/
public class ExampleHandler1 implements FilteredStatusHandler {
    private static final Logger LOG = Logger.getLogger(ExampleHandler1.class.getName());

    public void initialize(){
        LOG.info("Constructing ExampleHandler1");

    }
    @Override
    public ConfigPathFilter getFilter() {
        return new ConfigPathFilter("gpi:status1");
    }

    @Override
    public String getName() {
        return "ExampleHandler1";
    }

    @Override
    public <T> void update(StatusItem<T> item) {
        LOG.info(item.toString());
    }
}

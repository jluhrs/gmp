package edu.gemini.aspen.gpi;

import edu.gemini.aspen.giapi.status.Mapper;
import edu.gemini.aspen.giapi.status.StatusItem;

import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Apr 13, 2010
 * Time: 6:15:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilterMapper implements Mapper<String,Filter> {

    private static final Logger LOG = Logger.getLogger(FilterMapper.class.getName());
    @Override
    public Filter extract(StatusItem<String> item) {

        String val = item.getValue();
        try {
            return Filter.valueOf(val);
        } catch (IllegalArgumentException ex) {
            LOG.severe("Invalid Argument, not a filter: " + val);
            return null;
        }


    }

}

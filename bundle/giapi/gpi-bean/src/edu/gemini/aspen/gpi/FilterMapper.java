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
public class FilterMapper implements Mapper<Filter> {

    private static final Logger LOG = Logger.getLogger(FilterMapper.class.getName());
    @Override
    public Filter extract(StatusItem item) {

        Object o = item.getValue();

        if (o instanceof String) {
            String val = (String)o;
            try {
                return Filter.valueOf(val);
            } catch (IllegalArgumentException ex) {
                LOG.severe("Invalid Argument, not a filter: " + val);
                return null;
            }
        }
        return null;
    }

}

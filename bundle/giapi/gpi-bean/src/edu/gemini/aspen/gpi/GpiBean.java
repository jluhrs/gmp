package edu.gemini.aspen.gpi;

import edu.gemini.aspen.giapi.status.beans.Status;
import edu.gemini.aspen.giapi.status.beans.BaseStatusBean;
import edu.gemini.aspen.giapi.status.mappers.IntMapper;

/**
 * Created by IntelliJ IDEA.
 * User: anunez
 * Date: Apr 13, 2010
 * Time: 6:10:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class GpiBean extends BaseStatusBean {

    public static final String FILTER_PROP = "filter";
    public static final String STATUS_PROP = "status1";
    public static final String STATUS_PROP2 = "status1";


    @Status
    (statusName = "gpi:filter",
     mapper = FilterMapper.class)
    private Filter filter;

    @Status
    (statusName = "gpi:status1",
     mapper = IntMapper.class)
    private int status1;

    public int getStatus2() {
        return status2;
    }

    public void setStatus2(int status2) {
        int old = this.status2;
        this.status2 = status2;
        pcs.firePropertyChange(STATUS_PROP2, old, status2);
    }

    @Status
    (statusName = "gpi:status2",
     mapper = IntMapper.class)
    private int status2;



    public int getStatus1() {
        return status1;
    }

    public void setStatus1(int status1) {
        int old = this.status1;
        this.status1 = status1;
        pcs.firePropertyChange(STATUS_PROP, old, status1);
    }

    public GpiBean() {
        filter = Filter.BLUE; //default
        status1 = 0;
        status2 = 0;
    }

    public Filter getFilter() {
        return filter;
    }
    
    public void setFilter(Filter f) {
        Filter old = filter;
        filter = f;
        pcs.firePropertyChange(FILTER_PROP, old, filter);
    }


}

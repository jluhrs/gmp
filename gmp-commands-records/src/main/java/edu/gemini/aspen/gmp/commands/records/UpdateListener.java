package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.ChannelListener;
import gov.aps.jca.dbr.DBR;

import java.util.concurrent.CountDownLatch;

/**
* This listener is to be used when someone wants to wait for a value change in any Channel.
*
* @author Nicolas A. Barriga
*         Date: 3/24/11
*/
class UpdateListener extends CountDownLatch implements ChannelListener {

    public UpdateListener() {
        super(1);
    }

    private DBR dbr = null;

    @Override
    public void valueChange(DBR dbr) {
        if (getCount() == 1) {
            this.dbr = dbr;
            countDown();
        }
    }

    public DBR getDBR() {
        return dbr;
    }

}

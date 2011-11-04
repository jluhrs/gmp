package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.epics.api.ChannelListener;
import gov.aps.jca.dbr.DBR;

import java.util.concurrent.CountDownLatch;

/**
 * This listener is to be used when someone wants to wait for a value change in any Channel.
 * <p/>
 * Implementation details: this listener is a CountDownLatch of size 1, meaning that after 1 update, the latch will be
 * released. Inheriting from CountDownLatch looks much cleaner than delegation/composition.
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

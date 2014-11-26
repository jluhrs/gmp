package edu.gemini.gmp.commands.records;

import edu.gemini.epics.api.Channel;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.TimeoutException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class CarRecordTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/11/11
 */
public class CarRecordTest {
    private ChannelAccessServerImpl cas;
    private final String carPrefix = "gpi:applyC";

    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();
    }

    @After
    public void tearDown() throws CAException {
        cas.stop();
    }

    @Test
    public void carTest() throws CAException, TimeoutException {
        CarRecord car = new CarRecord(cas, carPrefix);
        car.start();

        Channel<CarRecord.Val> val = cas.createChannel(carPrefix + ".VAL", CarRecord.Val.IDLE);
        Channel<String> omss = cas.createChannel(carPrefix + ".OMSS", "");
        Channel<Integer> oerr = cas.createChannel(carPrefix + ".OERR", 0);
        Channel<Integer> clid = cas.createChannel(carPrefix + ".CLID", 0);

        car.changeState(CarRecord.Val.BUSY, "a", -1, 1);
        assertEquals(CarRecord.Val.BUSY, val.getFirst());
        assertEquals("a", omss.getFirst());
        assertEquals(new Integer(-1), oerr.getFirst());
        assertEquals(new Integer(1), clid.getFirst());

        car.stop();

        try {
            val.getAll();
            fail();
        } catch (IllegalStateException e) {
            //ok
        }

    }

}

package edu.gemini.aspen.gmp.commands.records;

import com.google.common.collect.Lists;
import edu.gemini.aspen.giapi.commands.*;
import edu.gemini.aspen.gmp.epics.top.EpicsTop;
import edu.gemini.aspen.gmp.epics.top.EpicsTopImpl;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.api.ChannelListener;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBRType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Class CadTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/11/11
 */
public class CadTest {
    private static final Logger LOG = Logger.getLogger(CadTest.class.getName());

    private ChannelAccessServerImpl cas;
    private final EpicsTop epicsTop = new EpicsTopImpl("gpi");
    private final String cadName = "observe";
    private CommandSender cs = MockFactory.createCommandSenderMock(epicsTop, cadName);

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
    public void cadTest() throws CAException, InterruptedException, TimeoutException {
        CadRecordImpl cad = new CadRecordImpl(cas, cs, epicsTop, cadName, Lists.newArrayList(cadName + ".DATA_LABEL"));
        cad.start();

        //test mark
        Channel<String> a = cas.createChannel(epicsTop.buildChannelName(cadName + ".DATA_LABEL"), "");
        Channel<CarRecord.Val> carVal = cas.createChannel(epicsTop.buildChannelName(cadName + "C.VAL"), CarRecord.Val.IDLE);


        class CarListener extends CountDownLatch implements ChannelListener<CarRecord.Val> {

            public CarListener() {
                super(2);
            }

            @Override
            public void valueChanged(String channelName, List<CarRecord.Val> values) {
                if (getCount() == 2 && CarRecord.Val.BUSY.equals(values.get(0))) {
                    countDown();
                }
                if (getCount() == 1 && CarRecord.Val.IDLE.equals(values.get(0))) {
                    countDown();
                }

            }

        }

        CarListener carListener = new CarListener();
        carVal.registerListener(carListener);

        a.setValue("label");

        assertEquals(CadState.MARKED, cad.getState());


        //test CAR
        Channel<Dir> dir = cas.createChannel(epicsTop.buildChannelName(cadName + ".DIR"), Dir.CLEAR);
        dir.setValue(Dir.MARK);
        dir.setValue(Dir.PRESET);
        if (!carListener.await(1, TimeUnit.SECONDS)) {
            fail();
        }

        carVal.unRegisterListener(carListener);
        cad.stop();

    }

    private void setDir(Dir d, Integer expectedState, Channel<Dir> dir, CadRecordImpl cad) throws BrokenBarrierException, InterruptedException, CAException, TimeoutException {
        dir.setValue(d);
        assertEquals(CadState.values()[expectedState], cad.getState());

    }

    @Test
    public void cadStateTransitionTest() throws CAException, BrokenBarrierException, InterruptedException, TimeoutException {
        CadRecordImpl cad = new CadRecordImpl(cas, cs, epicsTop, cadName, Lists.newArrayList(cadName + ".DATA_LABEL"));
        cad.start();
        Channel<String> a = cas.createChannel(epicsTop.buildChannelName(cadName + ".DATA_LABEL"), "");
        a.setValue("label");

        Channel<Dir> dir = cas.createChannel(epicsTop.buildChannelName(cadName + ".DIR"), Dir.CLEAR);


        //0 -> clear -> 0
        setDir(Dir.CLEAR, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> clear -> 0
        setDir(Dir.CLEAR, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> stop -> 0
        setDir(Dir.STOP, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET, 2, dir, cad);
        //2 -> clear -> 0
        setDir(Dir.CLEAR, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET, 2, dir, cad);
        //2 -> stop -> 0
        setDir(Dir.STOP, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET, 2, dir, cad);
        //2 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET, 2, dir, cad);
        //2 -> preset -> 2
        setDir(Dir.PRESET, 2, dir, cad);
        //2 -> start -> 0
        setDir(Dir.START, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> start -> 2->0
        setDir(Dir.START, 0, dir, cad);

    }
}

package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelListener;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import gov.aps.jca.CAStatusException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBRType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Class RecordsTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/22/11
 */
public class RecordsTest {
    private static final Logger LOG = Logger.getLogger(RecordsTest.class.getName());

    private ChannelAccessServerImpl cas;
    private final String carPrefix =  "gpitest:applyC";
    private final String prefix = "gpitest:";
    private final String cadName = "observe";
    private final String applyName = "apply";


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
    public void CARTest() throws CAException {
        CARRecord car = new CARRecord(cas, carPrefix);
        car.start();

        Channel<CARRecord.Val> val = cas.createChannel(carPrefix+".VAL",CARRecord.Val.UNKNOWN);
        Channel<String> omss = cas.createChannel(carPrefix+".OMSS","");
        Channel<Integer> oerr = cas.createChannel(carPrefix+".OERR",0);
        Channel<Integer> clid = cas.createChannel(carPrefix+".CLID",0);

        car.changeState(CARRecord.Val.BUSY, "a",-1,1);
        assertEquals(CARRecord.Val.BUSY, val.getFirst());
        assertEquals("a",omss.getFirst());
        assertEquals(new Integer(-1),oerr.getFirst());
        assertEquals(new Integer(1),clid.getFirst());

        car.stop();

        try{
            val.getVal();
            fail();
        }catch(IllegalStateException e){
            //ok
        }

    }

    private class ChangeListener extends CountDownLatch implements ChannelListener{

        public ChangeListener() {
            super(1);
        }

        @Override
        public void valueChange(DBR dbr) {
           countDown();
        }

    }

    @Test
    public void CADTest() throws CAException, InterruptedException {
        CADRecordImpl cad = new CADRecordImpl(cas,prefix,cadName,3);
        cad.start();

        //test mark
        Channel<String> a = cas.createChannel(prefix+cadName+".A","");
        Channel<Integer> mark = cas.createChannel(prefix+cadName+".MARK",0);
        Channel<CARRecord.Val> carVal = cas.createChannel(prefix+cadName+"C.VAL",CARRecord.Val.IDLE);


        ChangeListener listener = new ChangeListener();
        mark.registerListener(listener);

        class CARListener extends CountDownLatch implements ChannelListener {

            public CARListener() {
                super(2);
            }

            @Override
            public void valueChange(DBR dbr) {
                try {
                    if (getCount() == 2 && "BUSY".equals(((String[]) dbr.convert(DBRType.STRING).getValue())[0])) {
                        countDown();
                    }
                    if (getCount() == 1 && "IDLE".equals(((String[]) dbr.convert(DBRType.STRING).getValue())[0])) {
                        countDown();
                    }
                } catch (CAStatusException e) {
                    LOG.severe(e.getMessage());
                }

            }

        }

        CARListener carListener = new CARListener();
        carVal.registerListener(carListener);

        a.setValue("anything");

        if(listener.await(1, TimeUnit.SECONDS)){
            assertEquals(new Integer(1), mark.getFirst());
        }else{
            fail();
        }

        //test CAR
        Channel<Record.Dir> dir = cas.createChannel(prefix+cadName+".DIR",Record.Dir.CLEAR);
        dir.setValue(Record.Dir.MARK);
        if(!carListener.await(1, TimeUnit.SECONDS)){
            fail();
        }


        cad.stop();

    }

    private void setDir(Record.Dir d, Integer expectedState, Channel<Record.Dir> dir, Channel<Integer> mark) throws BrokenBarrierException, InterruptedException, CAException {
        ChangeListener listener = new ChangeListener();
        mark.registerListener(listener);
        dir.setValue(d);
        if (listener.await(1, TimeUnit.SECONDS)) {
            assertEquals(expectedState, mark.getFirst());
        } else {
            fail();
        }
        mark.unRegisterListener(listener);
    }

    @Test
    public void CADStateTransitionTest() throws CAException, BrokenBarrierException, InterruptedException {
        CADRecordImpl cad = new CADRecordImpl(cas,prefix,cadName,3);
        cad.start();

        Channel<Record.Dir> dir = cas.createChannel(prefix+cadName+".DIR",Record.Dir.CLEAR);
        Channel<Integer> mark = cas.createChannel(prefix+cadName+".MARK",0);


        //0 -> clear -> 0
        setDir(Record.Dir.CLEAR, 0, dir, mark);
        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> clear -> 0
        setDir(Record.Dir.CLEAR,0,dir,mark);
        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> stop -> 0
        setDir(Record.Dir.STOP,0,dir,mark);
        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> preset -> 2
        setDir(Record.Dir.PRESET,2,dir,mark);
        //2 -> clear -> 0
        setDir(Record.Dir.CLEAR,0,dir,mark);
        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> preset -> 2
        setDir(Record.Dir.PRESET,2,dir,mark);
        //2 -> stop -> 0
        setDir(Record.Dir.STOP,0,dir,mark);
        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> preset -> 2
        setDir(Record.Dir.PRESET,2,dir,mark);
        //2 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> preset -> 2
        setDir(Record.Dir.PRESET,2,dir,mark);
        //2 -> preset -> 2
        setDir(Record.Dir.PRESET,2,dir,mark);
        //2 -> start -> 0
        setDir(Record.Dir.START,0,dir,mark);


        class StartListener extends CountDownLatch implements ChannelListener {
            public StartListener() {
                super(2);
            }

            @Override
            public void valueChange(DBR dbr) {
                if (getCount() == 2 && ((int[]) dbr.getValue())[0] == 2) {
                    countDown();
                }
                if (getCount() == 1 && ((int[]) dbr.getValue())[0] == 0) {
                    countDown();
                }
            }
        }

        //0 -> mark -> 1
        setDir(Record.Dir.MARK,1,dir,mark);
        //1 -> start -> 2->0
        StartListener listener = new StartListener();
        mark.registerListener(listener);
        dir.setValue(Record.Dir.START);
        if (!listener.await(1, TimeUnit.SECONDS)) {
            fail();
        }
        mark.unRegisterListener(listener);

        cad.stop();

    }

    @Test
    public void applyTest() throws CAException {
        ApplyRecord apply = new ApplyRecord(cas,prefix,applyName);
        apply.start();
        Channel<Record.Dir> dir = cas.createChannel(prefix+applyName+".DIR",Record.Dir.CLEAR);
        CADRecordImpl cad = new CADRecordImpl(cas,prefix,cadName,3);
        cad.start();
        apply.bindCAD(cad);


        //test cad state changes
        cad.setDir(Record.Dir.MARK);
        dir.setValue(Record.Dir.START);
        assertEquals(1,cad.getClientId());
        assertEquals(new Integer(0), cad.getVal());

        apply.unBindCAD(cad);
        cad.stop();
        apply.stop();
    }
}

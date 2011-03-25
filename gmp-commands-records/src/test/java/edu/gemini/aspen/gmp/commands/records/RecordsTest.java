package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.aspen.giapi.commands.*;
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
import org.mockito.Matchers;

import java.util.concurrent.*;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
    private final String prefix = "gpitest";
    private final String cadName = "observe";
    private CommandSender cs;


    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();
        cs = mock(CommandSender.class);
        Command start = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.START);
        Command preset = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.PRESET);
        when(cs.sendCommand(eq(preset), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.ACCEPTED);
        when(cs.sendCommand(eq(start), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.COMPLETED);
        when(cs.sendCommand(Matchers.<Command>any(), Matchers.<CompletionListener>any(), anyLong())).thenReturn(HandlerResponse.ACCEPTED);
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
        CADRecordImpl cad = new CADRecordImpl(cas,cs,prefix,cadName,3);
        cad.start();

        //test mark
        Channel<String> a = cas.createChannel(prefix+":"+cadName+".A","");
        Channel<CARRecord.Val> carVal = cas.createChannel(prefix+":"+cadName+"C.VAL",CARRecord.Val.IDLE);


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

        assertEquals(CadState.MARKED, cad.getState());


        //test CAR
        Channel<Dir> dir = cas.createChannel(prefix+":"+cadName+".DIR", Dir.CLEAR);
        dir.setValue(Dir.MARK);
        if(!carListener.await(1, TimeUnit.SECONDS)){
            fail();
        }


        cad.stop();

    }

    private void setDir(Dir d, Integer  expectedState, Channel<Dir> dir, CADRecordImpl cad) throws BrokenBarrierException, InterruptedException, CAException {
        dir.setValue(d);
        assertEquals(CadState.values()[expectedState], cad.getState());

    }

    @Test
    public void CADStateTransitionTest() throws CAException, BrokenBarrierException, InterruptedException {
        CADRecordImpl cad = new CADRecordImpl(cas,cs,prefix,cadName,3);
        cad.start();

        Channel<Dir> dir = cas.createChannel(prefix+":"+cadName+".DIR", Dir.CLEAR);


        //0 -> clear -> 0
        setDir(Dir.CLEAR, 0, dir, cad);
        //0 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> clear -> 0
        setDir(Dir.CLEAR,0,dir,cad);
        //0 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> stop -> 0
        setDir(Dir.STOP,0,dir,cad);
        //0 -> mark -> 1
        setDir(Dir.MARK, 1, dir, cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET,2,dir,cad);
        //2 -> clear -> 0
        setDir(Dir.CLEAR,0,dir,cad);
        //0 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET,2,dir,cad);
        //2 -> stop -> 0
        setDir(Dir.STOP,0,dir,cad);
        //0 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET,2,dir,cad);
        //2 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> preset -> 2
        setDir(Dir.PRESET,2,dir,cad);
        //2 -> preset -> 2
        setDir(Dir.PRESET,2,dir,cad);
        //2 -> start -> 0
        setDir(Dir.START,0,dir,cad);
        //0 -> mark -> 1
        setDir(Dir.MARK,1,dir,cad);
        //1 -> start -> 2->0
        setDir(Dir.START,0,dir,cad);

    }

    @Test
    public void applyTest() throws CAException, InterruptedException {
        ApplyRecord apply = new ApplyRecord(cas,prefix);
        apply.start();
        Channel<Dir> dir = cas.createChannel(prefix+":apply.DIR", Dir.CLEAR);
        Channel<Integer> val = cas.createChannel(prefix+":apply.VAL",0);
        Channel<Integer> cadVal = cas.createChannel(prefix+":"+cadName+".VAL",0);
        Channel<Integer> clid = cas.createChannel(prefix+":apply.CLID",0);
        Channel<Integer> cadClid = cas.createChannel(prefix+":"+cadName+".ICID",0);

        CADRecordImpl cad = new CADRecordImpl(cas,cs,prefix,cadName,3);
        cad.start();
        apply.bindCAD(cad);


        //test cad state changes
        cad.getEpicsCad().setDir(Dir.MARK, clid.getFirst());
        dir.setValue(Dir.START);
        assertEquals(new Integer(1), clid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(1), val.getFirst());


        cad.getEpicsCad().setDir(Dir.MARK, clid.getFirst());
        dir.setValue(Dir.START);
        assertEquals(new Integer(2), cadClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(2),val.getFirst());

        apply.unBindCAD(cad);
        cad.stop();
        apply.stop();
    }
}

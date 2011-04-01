package edu.gemini.aspen.gmp.commands.records;

import com.google.common.collect.Lists;
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
        Command start = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.START, DefaultConfiguration.configurationBuilder().
                withConfiguration("gpitest:observe.A","").
                withConfiguration("gpitest:observe.B","").
                withConfiguration("gpitest:observe.C", "").
                build());
        Command preset = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.PRESET, DefaultConfiguration.configurationBuilder().
                withConfiguration("gpitest:observe.A","").
                withConfiguration("gpitest:observe.B","").
                withConfiguration("gpitest:observe.C","").
                build());
        Command cancel = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.CANCEL, DefaultConfiguration.configurationBuilder().
                withConfiguration("gpitest:observe.A","").
                withConfiguration("gpitest:observe.B","").
                withConfiguration("gpitest:observe.C","").
                build());
        Command preset_start = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.PRESET_START, DefaultConfiguration.configurationBuilder().
                withConfiguration("gpitest:observe.A","").
                withConfiguration("gpitest:observe.B","").
                withConfiguration("gpitest:observe.C","").
                build());
        when(cs.sendCommand(eq(preset), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.ACCEPTED);
        when(cs.sendCommand(eq(start), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.COMPLETED);
        when(cs.sendCommand(eq(cancel), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.ACCEPTED);
        when(cs.sendCommand(eq(preset_start), Matchers.<CompletionListener>any())).thenReturn(HandlerResponse.COMPLETED);
        when(cs.sendCommand(Matchers.<Command>any(), Matchers.<CompletionListener>any(), anyLong())).thenReturn(HandlerResponse.ACCEPTED);
    }

    @After
    public void tearDown() throws CAException {
        cas.stop();
    }

    @Test
    public void CarTest() throws CAException {
        CarRecord car = new CarRecord(cas, carPrefix);
        car.start();

        Channel<CarRecord.Val> val = cas.createChannel(carPrefix+".VAL", CarRecord.Val.UNKNOWN);
        Channel<String> omss = cas.createChannel(carPrefix+".OMSS","");
        Channel<Integer> oerr = cas.createChannel(carPrefix+".OERR",0);
        Channel<Integer> clid = cas.createChannel(carPrefix+".CLID",0);

        car.changeState(CarRecord.Val.BUSY, "a",-1,1);
        assertEquals(CarRecord.Val.BUSY, val.getFirst());
        assertEquals("a",omss.getFirst());
        assertEquals(new Integer(-1),oerr.getFirst());
        assertEquals(new Integer(1),clid.getFirst());

        car.stop();

        try{
            val.getAll();
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
    public void CadTest() throws CAException, InterruptedException {
        CadRecordImpl cad = new CadRecordImpl(cas,cs,prefix,cadName,"A,B,C");
        cad.start();

        //test mark
        Channel<String> a = cas.createChannel(prefix+":"+cadName+".A","");
        Channel<CarRecord.Val> carVal = cas.createChannel(prefix+":"+cadName+"C.VAL", CarRecord.Val.IDLE);


        class CarListener extends CountDownLatch implements ChannelListener {

            public CarListener() {
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

        CarListener carListener = new CarListener();
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

    private void setDir(Dir d, Integer  expectedState, Channel<Dir> dir, CadRecordImpl cad) throws BrokenBarrierException, InterruptedException, CAException {
        dir.setValue(d);
        assertEquals(CadState.values()[expectedState], cad.getState());

    }

    @Test
    public void CadStateTransitionTest() throws CAException, BrokenBarrierException, InterruptedException {
        CadRecordImpl cad = new CadRecordImpl(cas,cs,prefix,cadName,"A,B,C");
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

        CadRecordImpl cad = new CadRecordImpl(cas,cs,prefix,cadName, "A,B,C");
        cad.start();
        apply.bindCad(cad);


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

        apply.unBindCad(cad);
        cad.stop();
        apply.stop();
    }
}

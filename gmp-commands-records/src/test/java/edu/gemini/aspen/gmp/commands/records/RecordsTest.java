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

import java.io.*;
import java.util.concurrent.*;
import java.util.logging.Level;
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
    public static final String xmlStr;

    public static final String xsdStr;


    static {
        BufferedReader in = new BufferedReader(new InputStreamReader(RecordsTest.class.getResourceAsStream("../../../../../../giapi-apply-config.xml")));
        String xml = "";
        try {

            String line = in.readLine();
            while (line != null) {
                xml += line;
                line = in.readLine();

            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        xmlStr = xml;

        in = new BufferedReader(new InputStreamReader(RecordsTest.class.getResourceAsStream("../../../../../../giapi-apply-config.xsd")));
        String xsd = "";
        try {
            String line = in.readLine();
            while (line != null) {
                xsd += line;
                line = in.readLine();

            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        xsdStr = xsd;

    }

    private ChannelAccessServerImpl cas;
    private final String carPrefix = "gpi:applyC";
    private final String epicsTop = "gpi";
    private final String cadName = "observe";
    private CommandSender cs;


    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();
        cs = mock(CommandSender.class);
        Command start = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.START, DefaultConfiguration.configurationBuilder().
                withConfiguration(epicsTop + ":" + cadName + ".DATA_LABEL", "").
                build());
        Command preset = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.PRESET, DefaultConfiguration.configurationBuilder().
                withConfiguration(epicsTop + ":" + cadName + ".DATA_LABEL", "").
                build());
        Command cancel = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.CANCEL, DefaultConfiguration.configurationBuilder().
                withConfiguration(epicsTop + ":" + cadName + ".DATA_LABEL", "").
                build());
        Command preset_start = new Command(SequenceCommand.valueOf(cadName.toUpperCase()), Activity.PRESET_START, DefaultConfiguration.configurationBuilder().
                withConfiguration(epicsTop + ":" + cadName + ".DATA_LABEL", "").
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

        Channel<CarRecord.Val> val = cas.createChannel(carPrefix + ".VAL", CarRecord.Val.UNKNOWN);
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

    private class ChangeListener extends CountDownLatch implements ChannelListener {

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
        CadRecordImpl cad = new CadRecordImpl(cas, cs, epicsTop, cadName, Lists.newArrayList(cadName + ".DATA_LABEL"));
        cad.start();

        //test mark
        Channel<String> a = cas.createChannel(epicsTop + ":" + cadName + ".DATA_LABEL", "");
        Channel<CarRecord.Val> carVal = cas.createChannel(epicsTop + ":" + cadName + "C.VAL", CarRecord.Val.IDLE);


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

        a.setValue("");

        assertEquals(CadState.MARKED, cad.getState());


        //test CAR
        Channel<Dir> dir = cas.createChannel(epicsTop + ":" + cadName + ".DIR", Dir.CLEAR);
        dir.setValue(Dir.MARK);
        dir.setValue(Dir.PRESET);
        if (!carListener.await(1, TimeUnit.SECONDS)) {
            fail();
        }


        cad.stop();

    }

    private void setDir(Dir d, Integer expectedState, Channel<Dir> dir, CadRecordImpl cad) throws BrokenBarrierException, InterruptedException, CAException {
        dir.setValue(d);
        assertEquals(CadState.values()[expectedState], cad.getState());

    }

    @Test
    public void CadStateTransitionTest() throws CAException, BrokenBarrierException, InterruptedException {
        CadRecordImpl cad = new CadRecordImpl(cas, cs, epicsTop, cadName, Lists.newArrayList(cadName + ".DATA_LABEL"));
        cad.start();

        Channel<Dir> dir = cas.createChannel(epicsTop + ":" + cadName + ".DIR", Dir.CLEAR);


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

    @Test
    public void applyTest() throws CAException, InterruptedException, IOException {
        File xml = null;

        xml = File.createTempFile("ApplyTest", ".xml");

        File xsd = null;
        xsd = File.createTempFile("ApplyTest", ".xsd");

        FileWriter xmlWrt = new FileWriter(xml);
        FileWriter xsdWrt = new FileWriter(xsd);

        xmlWrt.write(xmlStr);
        xsdWrt.write(xsdStr);
        xmlWrt.close();
        xsdWrt.close();


        ApplyRecord apply = new ApplyRecord(cas, cs, xml.getPath(), xsd.getPath());
        apply.start();
        Channel<Dir> dir = cas.createChannel(epicsTop + ":apply.DIR", Dir.CLEAR);
        Channel<Integer> val = cas.createChannel(epicsTop + ":apply.VAL", 0);
        Channel<Integer> cadVal = cas.createChannel(epicsTop + ":" + cadName + ".VAL", 0);
        Channel<Integer> clid = cas.createChannel(epicsTop + ":apply.CLID", 0);
        Channel<Integer> cadClid = cas.createChannel(epicsTop + ":" + cadName + ".ICID", 0);
        Channel<String> data_label = cas.createChannel(epicsTop + ":" + cadName + ".DATA_LABEL", "");


        data_label.setValue("");
        dir.setValue(Dir.START);
        assertEquals(new Integer(1), clid.getFirst());
        assertEquals(new Integer(1), cadClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(1), val.getFirst());

        //special record apply/config.
        Channel<String> useAo = cas.createChannel(epicsTop + ":configAo:useAo", "");
        cadClid = cas.createChannel(epicsTop + ":config.ICID", 0);
        cadVal = cas.createChannel(epicsTop + ":config.VAL", 0);


        useAo.setValue("1");
        dir.setValue(Dir.START);
        assertEquals(new Integer(2), cadClid.getFirst());
        assertEquals(new Integer(0), cadVal.getFirst());
        assertEquals(new Integer(2), val.getFirst());

        apply.stop();
    }
}

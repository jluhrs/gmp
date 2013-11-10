package edu.gemini.cas;

import com.google.common.collect.ImmutableList;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.dbr.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;
import java.util.logging.Logger;

/**
 * Class CasTest
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class CasTest {
    private static final Logger LOG = Logger.getLogger(CasTest.class.getName());
    private String varname = "nico:test1";
    private ChannelAccessServerImpl giapicas;

    @Before
    public void setUp() throws Exception {
        giapicas = new ChannelAccessServerImpl();
        giapicas.start();
    }

    @After
    public void tearDown() throws Exception {
        Thread.sleep(20);//can't start and stop immediately, and our tests are too short
        giapicas.stop();
    }

    /**
     * Just starts and stops the server
     */
    @Test
    public void testStartStop() throws Exception {

        Thread.sleep(20);//can't start and stop immediately
        giapicas.stop();

        giapicas.start();
        try {
            giapicas.start();
            fail();
        } catch (IllegalStateException ex) {

        }
    }

    /**
     * Starts the server, adds a PV, reads from it, removes the PV, checks it is correctly removed and stops the server
     */
    @Test
    public void testAddTwice() throws Exception {

        Channel ch = giapicas.createChannel(varname, 2);
        DBR dbr = ch.getDBR();


        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(2, objarr[0]);

        Channel ch2 = giapicas.createChannel(varname, 1);
        ch2.setValue(2);
        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createChannel(varname, 2.0f);
        ch2 = giapicas.createChannel(varname, 1.0f);
        ch2.setValue(2.0f);
        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createChannel(varname, 2.0);
        ch2 = giapicas.createChannel(varname, 1.0);
        ch2.setValue(2.0);
        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createChannel(varname, "2");
        ch2 = giapicas.createChannel(varname, "1");
        ch2.setValue("2");
        assertEquals(ch, ch2);
        giapicas.destroyChannel(ch2);
        try {
            ch.getDBR();
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
    }

    @Test
    public void testAddTwiceWrongType() throws Exception {

        Channel ch = giapicas.createChannel(varname, 0);
        DBR dbr = ch.getDBR();


        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(0, objarr[0]);

        try {
            Channel ch2 = giapicas.createChannel(varname, 1.0f);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            Channel ch2 = giapicas.createChannel(varname, 1.0);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            Channel ch2 = giapicas.createChannel(varname, "1");
            fail();
        } catch (RuntimeException ex) {
            //OK
        }

        giapicas.destroyChannel(ch);

    }

    /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteVar() throws Exception {
        Channel<Integer> ch = giapicas.createChannel(varname, 1);
        ch.setValue(3);

        DBR dbr = ch.getDBR();


        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(3, objarr[0]);

    }

    /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteArray() throws Exception {
        Channel<Integer> ch = giapicas.createChannel(varname, ImmutableList.of(0, 0, 0));
        ch.setValue(ImmutableList.of(3, 4, 5));

        DBR dbr = ch.getDBR();

        int num = dbr.getCount();
        assertEquals(3, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(3, objarr[0]);
        assertEquals(4, objarr[1]);
        assertEquals(5, objarr[2]);

    }

    /**
     * Starts the server, adds a Double PV, writes an array, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteDoubleArray() throws Exception {
        Channel<Double> ch = giapicas.createChannel(varname, ImmutableList.of(0.0, 0.0, 0.0));
        ch.setValue(ImmutableList.of(3.0, 4.0, 5.0));

        DBR dbr = ch.getDBR();

        int num = dbr.getCount();
        assertEquals(3, num);
        Object obj = dbr.getValue();
        double[] objarr = (double[]) obj;
        assertEquals(3.0, objarr[0], 0.0);
        assertEquals(4.0, objarr[1], 0.0);
        assertEquals(5.0, objarr[2], 0.0);
    }

    /**
     * Starts the server, adds a Float PV, writes an array, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteFloatArray() throws Exception {
        Channel<Float> ch = giapicas.createChannel(varname, ImmutableList.of(0.0f, 0.0f, 0.0f));
        ch.setValue(ImmutableList.of(3.0f, 4.0f, 5.0f));

        DBR dbr = ch.getDBR();

        int num = dbr.getCount();
        assertEquals(3, num);
        Object obj = dbr.getValue();
        float[] objarr = (float[]) obj;
        assertEquals(3.0, objarr[0], 0.0);
        assertEquals(4.0, objarr[1], 0.0);
        assertEquals(5.0, objarr[2], 0.0);

    }

    /**
     * Starts the server, adds one PV of each type, writes a value in each PV, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteVarAllTypes() throws Exception {
        Channel<Integer> chI = giapicas.createChannel("nico:int", 1);
        Channel<Float> chF = giapicas.createChannel("nico:float", 1.0f);
        Channel<Double> chD = giapicas.createChannel("nico:double", 1.0);
        Channel<String> chS = giapicas.createChannel("nico:string", "1");
        Channel<Byte> chB = giapicas.createChannel("nico:byte", (byte)127);

        chI.setValue(3);
        chF.setValue(3.0f);
        chD.setValue(3.0);
        chS.setValue("three");
        chB.setValue((byte)3);

        DBR dbr[] = new DBR[5];
        dbr[0] = chI.getDBR();
        dbr[1] = chF.getDBR();
        dbr[2] = chD.getDBR();
        dbr[3] = chS.getDBR();
        dbr[4] = chB.getDBR();
        Object ret[] = new Object[dbr.length];

        for (int i = 0; i < dbr.length; i++) {
            assertEquals(1, dbr[i].getCount());
            ret[i] = dbr[i].getValue();
        }
        assertEquals(3, ((int[]) ret[0])[0]);
        assertEquals(3f, ((float[]) ret[1])[0], 0.00001);
        assertEquals(3.0, ((double[]) ret[2])[0], 0.00001);
        assertEquals("three", ((String[]) ret[3])[0]);
        assertEquals((byte)3, ((byte[]) ret[4])[0]);

    }

    /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteWrongType() throws Exception {
        Channel ch = giapicas.createChannel(varname, 1);

        int exceptions = 0;

        try {
            ch.setValue(3.0f);
        } catch (IllegalArgumentException ex) {
            //correct
            exceptions++;
        }
        try {
            ch.setValue(3.0d);
        } catch (IllegalArgumentException ex) {
            //correct
            exceptions++;
        }
        try {
            ch.setValue("three");
        } catch (IllegalArgumentException ex) {
            //correct
            exceptions++;
        }
        try {
            ch.setValue(new Object());
        } catch (IllegalArgumentException ex) {
            //correct
            exceptions++;
        }
        try {
            ch.setValue(new Exception());
        } catch (IllegalArgumentException ex) {
            //correct
            exceptions++;
        }
        assertEquals(5, exceptions);

    }

    @Test
    public void testCreateAlarmChannels() throws Exception {
        AlarmChannel ch = giapicas.createAlarmChannel("int", 1);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("float", 1.0f);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("double", 1.0);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("string", "1");
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("int", 1);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("float", 1.0f);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("double", 1.0);
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("string", "1");
        giapicas.destroyChannel(ch);
        ch = giapicas.createAlarmChannel("enum", Dir.CLEAR);
        giapicas.destroyChannel(ch);
    }

    @Test
    public void testWriteAlarm() throws Exception {
        AlarmChannel<Integer> ch = giapicas.createAlarmChannel(varname, 1);
        ch.setAlarm(Status.HIHI_ALARM, Severity.MAJOR_ALARM, "alarm message");
        ch.setValue(3);
        DBR dbr = ch.getDBR();

        assertEquals(Severity.MAJOR_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.HIHI_ALARM, ((STS) dbr).getStatus());
        ch.clearAlarm();
        dbr = ch.getDBR();
        assertEquals(Severity.NO_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.NO_ALARM, ((STS) dbr).getStatus());

        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(3, objarr[0]);

        //test add again
        {
            Channel ch2 = giapicas.createAlarmChannel(varname, 1);
            assertEquals(ch, ch2);
        }

        //test add with same name, wrong type
        try {
            Channel ch2 = giapicas.createAlarmChannel(varname, 1.0f);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            Channel ch2 = giapicas.createAlarmChannel(varname, 1.0);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            Channel ch2 = giapicas.createAlarmChannel(varname, "1");
            fail();
        } catch (RuntimeException ex) {
            //OK
        }

    }

    private enum Dir {
        MARK,
        CLEAR,
        PRESET,
        START,
        STOP
    }

    private enum State {
        IDLE,
        PAUSED,
        BUSY,
        ERROR
    }

    @Test
    public void testEnumChannels() throws Exception {
        Dir d = Dir.MARK;
        Channel<Dir> ch = giapicas.createChannel("test", d);
        DBR dbr = ch.getDBR();

        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        short[] objarr = (short[]) obj;
        assertEquals(d.ordinal(), objarr[0]);
        obj = dbr.convert(DBRType.STRING).getValue();
        assertEquals(d.name(), ((String[]) obj)[0]);

        d = Dir.PRESET;
        ch.setValue(d);
        dbr = ch.getDBR();

        num = dbr.getCount();
        assertEquals(1, num);
        obj = dbr.getValue();
        objarr = (short[]) obj;
        assertEquals(d.ordinal(), objarr[0]);
        obj = dbr.convert(DBRType.STRING).getValue();
        assertEquals(d.name(), ((String[]) obj)[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEnumWrongType() throws Exception {
        Channel ch = giapicas.createChannel("test", Dir.MARK);
        ch.setValue(State.BUSY);
    }

    @Test
    public void testWriteEnumAlarm() throws Exception {
        AlarmChannel<Dir> ch = giapicas.createAlarmChannel(varname, Dir.CLEAR);
        ch.setAlarm(Status.HIHI_ALARM, Severity.MAJOR_ALARM, "alarm message");
        DBR dbr = ch.getDBR();

        assertEquals(Severity.MAJOR_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.HIHI_ALARM, ((STS) dbr).getStatus());
        ch.clearAlarm();
        dbr = ch.getDBR();
        assertEquals(Severity.NO_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.NO_ALARM, ((STS) dbr).getStatus());
    }

    @Test
    public void testGetVal() throws Exception {
        Channel<Integer> ch = giapicas.createChannel(varname, 1);
        ch.setValue(3);

        List<Integer> values = ch.getAll();


        assertEquals(1, values.size());
        assertEquals(new Integer(3), values.get(0));

    }

    @Test
    public void testIsValid() throws Exception {
        Channel<Integer> ch = giapicas.createChannel(varname, 1);
        Channel<Integer> ach = giapicas.createAlarmChannel(varname + "alarm", 1);

        assertTrue(ch.isValid());
        assertTrue(ach.isValid());

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ach);

        assertFalse(ch.isValid());
        assertFalse(ach.isValid());
    }

    @Test
    public void testDestroy() throws Exception {
        Channel<Integer> original = giapicas.createChannel(varname, 1);
        Channel<Integer> extraReference = giapicas.createChannel(varname, 1);
        assertTrue(original.isValid());
        assertTrue(extraReference.isValid());
        assertEquals(new Integer(1), original.getFirst());
        giapicas.destroyChannel(original);
        assertFalse(original.isValid());
        assertFalse(extraReference.isValid());

        Channel<Integer> newReference = giapicas.createChannel(varname, 2);
        assertTrue(newReference.isValid());
        assertEquals(new Integer(2), newReference.getFirst());
        assertFalse(original.isValid());
        assertFalse(extraReference.isValid());
    }


}

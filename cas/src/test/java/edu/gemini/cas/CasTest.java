package edu.gemini.cas;

import com.google.common.collect.ImmutableList;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.STS;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
    private ChannelAccessServer giapicas;

    @Before
    public void setUp() throws Exception{
        giapicas = new ChannelAccessServer();
        giapicas.start();
    }

    @After
    public void tearDown() throws Exception{
        giapicas.stop();
    }

    /**
     * Just starts and stops the server
     */
    @Test
    public void testStartStop() throws Exception {

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

        IChannel ch = giapicas.createIntegerChannel(varname, 1);
        DBR dbr = ch.getValue();


        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(0, objarr[0]);

        IChannel ch2 = giapicas.createIntegerChannel(varname, 1);

        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createFloatChannel(varname, 1);
        ch2 = giapicas.createFloatChannel(varname, 1);
        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createDoubleChannel(varname, 1);
        ch2 = giapicas.createDoubleChannel(varname, 1);
        assertEquals(ch, ch2);

        giapicas.destroyChannel(ch);
        giapicas.destroyChannel(ch2);
        ch = giapicas.createStringChannel(varname, 1);
        ch2 = giapicas.createStringChannel(varname, 1);
        assertEquals(ch, ch2);
        giapicas.destroyChannel(ch2);
        try {
            ch.getValue();
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
    }

    @Test
    public void testAddTwiceWrongType() throws Exception {

        IChannel ch = giapicas.createIntegerChannel(varname, 1);
        DBR dbr = ch.getValue();


        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(0, objarr[0]);

        try {
            IChannel ch2 = giapicas.createFloatChannel(varname, 1);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            IChannel ch2 = giapicas.createDoubleChannel(varname, 1);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            IChannel ch2 = giapicas.createStringChannel(varname, 1);
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
        IntegerChannel ch = giapicas.createIntegerChannel(varname, 1);
        ch.setValue(3);

        DBR dbr = ch.getValue();


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
        IntegerChannel ch = giapicas.createIntegerChannel(varname, 3);
        ch.setValue(ImmutableList.of(3,4,5));

        DBR dbr = ch.getValue();


        int num = dbr.getCount();
        assertEquals(3, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(3, objarr[0]);
        assertEquals(4, objarr[1]);
        assertEquals(5, objarr[2]);

    }

    /**
     * Starts the server, adds one PV of each type, writes a value in each PV, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteVarAllTypes() throws Exception {
        IntegerChannel chI = giapicas.createIntegerChannel("nico:int", 1);
        FloatChannel chF = giapicas.createFloatChannel("nico:float", 1);
        DoubleChannel chD = giapicas.createDoubleChannel("nico:double", 1);
        StringChannel chS = giapicas.createStringChannel("nico:string", 1);


        chI.setValue(3);
        chF.setValue(3.0f);
        chD.setValue(3.0);
        chS.setValue("three");


        DBR dbr[] = new DBR[4];
        dbr[0] = chI.getValue();
        dbr[1] = chF.getValue();
        dbr[2] = chD.getValue();
        dbr[3] = chS.getValue();
        Object ret[] = new Object[4];

        for (int i = 0; i < 4; i++) {
            assertEquals(1, dbr[i].getCount());
            ret[i] = dbr[i].getValue();
        }
        assertEquals(3, ((int[]) ret[0])[0]);
        assertEquals(3f, ((float[]) ret[1])[0], 0.00001);
        assertEquals(3.0, ((double[]) ret[2])[0], 0.00001);
        assertEquals("three", ((String[]) ret[3])[0]);

    }

    /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteWrongType() throws Exception {
        IChannel ch = giapicas.createIntegerChannel(varname, 1);

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
        assertEquals(3, exceptions);

    }

    @Test
    public void testCreateAlarmChannels() throws Exception {
        IAlarmChannel ch1 = giapicas.createIntegerAlarmChannel("int", 1);
        IAlarmChannel ch2 = giapicas.createFloatAlarmChannel("float", 1);
        IAlarmChannel ch3 = giapicas.createDoubleAlarmChannel("double", 1);
        IAlarmChannel ch4 = giapicas.createStringAlarmChannel("string", 1);
    }

    @Test
    public void testWriteAlarm() throws Exception {
        IntegerAlarmChannel ch = giapicas.createIntegerAlarmChannel(varname, 1);
        ch.setAlarm(Status.HIHI_ALARM, Severity.MAJOR_ALARM, "alarm message");
        ch.setValue(3);
        DBR dbr = ch.getValue();

        assertEquals(Severity.MAJOR_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.HIHI_ALARM, ((STS) dbr).getStatus());
        ch.clearAlarm();
        dbr = ch.getValue();
        assertEquals(Severity.NO_ALARM, ((STS) dbr).getSeverity());
        assertEquals(Status.NO_ALARM, ((STS) dbr).getStatus());

        int num = dbr.getCount();
        assertEquals(1, num);
        Object obj = dbr.getValue();
        int[] objarr = (int[]) obj;
        assertEquals(3, objarr[0]);

        //test add again
        {
            IChannel ch2 = giapicas.createIntegerAlarmChannel(varname, 1);
            assertEquals(ch, ch2);
        }

        //test add with same name, wrong type
        try {
            IChannel ch2 = giapicas.createFloatAlarmChannel(varname, 1);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            IChannel ch2 = giapicas.createDoubleAlarmChannel(varname, 1);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }
        try {
            IChannel ch2 = giapicas.createStringAlarmChannel(varname, 1);
            fail();
        } catch (RuntimeException ex) {
            //OK
        }

    }



}

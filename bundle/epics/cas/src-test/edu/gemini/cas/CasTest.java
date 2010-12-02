package edu.gemini.cas;

import gov.aps.jca.dbr.DBR;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class CasTest
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class CasTest {
    private static final Logger LOG = Logger.getLogger(CasTest.class.getName());
    private String varname="nico:test1";

    @Before
    public void setUp() {
    }

    /**
     * Just starts and stops the server
     */
    @Test
    public void testStartStop() {

        try {
            Cas giapicas = new Cas();


            giapicas.start();

            giapicas.stop();

            giapicas.start();

            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }
    }

    /**
     * Starts the server, adds a PV, reads from it, removes the PV, checks it is correctly removed and stops the server
     */
    @Test
    public void testAddTwice() {

        try {
            Cas giapicas = new Cas();
            giapicas.start();

            Cas.Channel ch= giapicas.createIntegerChannel(varname,1);
            DBR dbr = ch.getValue();



            int num = dbr.getCount();
            assertEquals(1, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(0, objarr[0]);

            Cas.Channel ch2= giapicas.createIntegerChannel(varname,1);

            assertEquals(ch,ch2);
            giapicas.destroyChannel(varname);

            try{
                ch.getValue();
                fail();
            }catch(RuntimeException ex){
                //OK   
            }finally{
                giapicas.stop();
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }
    }


    /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteVar() {
        try {
            Cas giapicas = new Cas();
            giapicas.start();
            Cas.Channel ch= giapicas.createIntegerChannel(varname,1);
            ch.setValue(3);

            DBR dbr = ch.getValue();



            int num = dbr.getCount();
            assertEquals(1, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(3, objarr[0]);

            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }


    }
   /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteArray() {
        try {
            Cas giapicas = new Cas();
            giapicas.start();
            Cas.Channel ch= giapicas.createIntegerChannel(varname,3);
            ch.setValue(new Integer[]{3,4,5});

            DBR dbr = ch.getValue();



            int num = dbr.getCount();
            assertEquals(3, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(3, objarr[0]);
            assertEquals(4, objarr[1]);
            assertEquals(5, objarr[2]);

            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }


    }

    /**
     * Starts the server, adds one PV of each type, writes a value in each PV, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteVarAllTypes() {
        try {
            Cas giapicas = new Cas();
            giapicas.start();
            Cas.Channel chI= giapicas.createIntegerChannel("nico:int",1);
            Cas.Channel chF= giapicas.createFloatChannel("nico:float",1);
            Cas.Channel chD= giapicas.createDoubleChannel("nico:double",1);
            Cas.Channel chS= giapicas.createStringChannel("nico:string",1);


            chI.setValue(3);
            chF.setValue(3.0f);
            chD.setValue(3.0);
            chS.setValue("three");


            DBR dbr[] = new DBR[4];
            dbr[0]=chI.getValue();
            dbr[1]=chF.getValue();
            dbr[2]=chD.getValue();
            dbr[3]=chS.getValue();
            Object ret[] = new Object[4];

            for(int i=0;i<4;i++){
                assertEquals(1, dbr[i].getCount());
                ret[i]=dbr[i].getValue();
            }
            assertEquals(3, ((int[])ret[0])[0]);
            assertEquals(3f, ((float[])ret[1])[0]);
            assertEquals(3.0, ((double[])ret[2])[0]);
            assertEquals("three", ((String[])ret[3])[0]);

            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }


    }
  /**
     * Starts the server, adds a PV, writes a value, reads it back, checks the value read is correct and stops the server
     */
    @Test
    public void testWriteWrongType() {
        try {
            Cas giapicas = new Cas();
            giapicas.start();
            Cas.Channel ch= giapicas.createIntegerChannel(varname,1);

            int exceptions=0;

            try{
                ch.setValue(3.0f);
            }catch(IllegalArgumentException ex){
                //correct
                exceptions++;
            }
            try{
                ch.setValue(3.0d);
            }catch(IllegalArgumentException ex){
                //correct
                exceptions++;
            }
            try{
                ch.setValue("three");
            }catch(IllegalArgumentException ex){
                //correct
                exceptions++;
            }
            assertEquals(3,exceptions);
            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
            fail();
        }


    }


    @After
    public void tearDown(){}
}

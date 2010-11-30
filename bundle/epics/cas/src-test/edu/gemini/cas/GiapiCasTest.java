package edu.gemini.cas;

import gov.aps.jca.dbr.DBR;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class GiapiCasTest
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class GiapiCasTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(GiapiCasTest.class.getName());
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
            GiapiCas giapicas = new GiapiCas();


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
    public void testAddRemoveVariable() {

        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();

            giapicas.<Integer>addVariable(varname,2);
            DBR dbr = giapicas.get(varname);



            int num = dbr.getCount();
            assertEquals(1, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(2, objarr[0]);

            giapicas.removeVariable(varname);

            try{
                giapicas.get(varname);
            }catch(IllegalArgumentException ex){
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
     * Starts the server, adds a PV, reads from it, checks the value read is correct and stops the server
     */
    @Test
    public void testReadVar() {
        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();
            giapicas.<Integer>addVariable(varname,2);


            DBR dbr = giapicas.get(varname);


            int num = dbr.getCount();
            assertEquals(1, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(2, objarr[0]);
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
    public void testWriteVar() {
        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();
            giapicas.<Integer>addVariable(varname,2);

            giapicas.<Integer>put(varname,3);

            DBR dbr = giapicas.get(varname);



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
    public void testWriteWrongType() {
        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();
            giapicas.<Integer>addVariable(varname,2);
            
            int exceptions=0;

            try{
                giapicas.put(varname,(float)3.0);
            }catch(IllegalArgumentException ex){
                //correct
                exceptions++;
            }
            try{
                giapicas.put(varname,new Double(3.0));
            }catch(IllegalArgumentException ex){
                //correct
                exceptions++;
            }
            try{
                giapicas.put(varname,"test");
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

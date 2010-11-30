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
    public void testWriteArray() {
        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();
            giapicas.<Integer>addVariable(varname,2,3,4);

            giapicas.<Integer>put(varname,3,4,5);

            DBR dbr = giapicas.get(varname);



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
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();
            giapicas.<Integer>addVariable("nico:int",2);
            giapicas.<Float>addVariable("nico:float",(float)2.0);
            giapicas.<Double>addVariable("nico:double",2.0);
            giapicas.<String>addVariable("nico:string","two");

            giapicas.<Integer>put("nico:int",3);
            giapicas.<Float>put("nico:float",(float)3);
            giapicas.<Double>put("nico:double",3.0);
            giapicas.<String>put("nico:string","three");

            DBR dbr[] = new DBR[4];
            dbr[0]=giapicas.get("nico:int");
            dbr[1]=giapicas.get("nico:float");
            dbr[2]=giapicas.get("nico:double");
            dbr[3]=giapicas.get("nico:string");
            Object ret[] = new Object[4];

            for(int i=0;i<4;i++){
                assertEquals(1, dbr[i].getCount());
                ret[i]=dbr[i].getValue();
            }
            assertEquals(3, ((int[])ret[0])[0]);
            assertEquals((float)3, ((float[])ret[1])[0]);
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

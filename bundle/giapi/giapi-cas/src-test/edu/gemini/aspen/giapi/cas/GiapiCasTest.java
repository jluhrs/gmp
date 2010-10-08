package edu.gemini.aspen.giapi.cas;

import gov.aps.jca.Channel;
import gov.aps.jca.Context;
import gov.aps.jca.JCALibrary;
import gov.aps.jca.TimeoutException;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_Int;
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
    private GiapiCas giapicas;
    private JCALibrary jca;
    @Before
    public void setUp() {
        jca = JCALibrary.getInstance();
    }

    /**
     * Just starts and stops the server
     */
    @Test
    public void testStartStop() {

        try {
            GiapiCas giapicas = new GiapiCas();
            giapicas.start();

           // Thread.sleep(500);

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
            giapicas.addVariable("nico:test1",DBR_Int.TYPE,new int[]{2});
            Context ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            Channel channel = ctxt.createChannel("nico:test1");
            ctxt.pendIO(1);
            DBR dbr = channel.get();
            ctxt.pendIO(1);


            int num = dbr.getCount();
            assertEquals(1, num);
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            assertEquals(2, objarr[0]);
            channel.destroy();

            giapicas.removeVariable("nico:test1");
            channel = ctxt.createChannel("nico:test1");
            try{
                ctxt.pendIO(1);
            }catch(TimeoutException ex){
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
            giapicas.addVariable("nico:test1",DBR_Int.TYPE,new int[]{2});



            Context ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            Channel channel = ctxt.createChannel("nico:test1");
            ctxt.pendIO(1);
            DBR dbr = channel.get();
            ctxt.pendIO(1);


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
            giapicas.addVariable("nico:test1", DBR_Int.TYPE,new int[]{2});

            //Thread.sleep(1000);
            Context ctxt = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            Channel channel = ctxt.createChannel("nico:test1");

            ctxt.pendIO(1);

            channel.put(3);
            channel.getContext().flushIO();
            ctxt.pendIO(1);


            DBR dbr = channel.get();
            ctxt.pendIO(1);


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

    @After
    public void tearDown(){}
}

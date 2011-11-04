package edu.gemini.cas;

import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class TestStress
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
@Ignore
public class TestStress {
    private static final Logger LOG = Logger.getLogger(TestStress.class.getName());
    private ChannelAccessServerImpl cas;
    private JCALibrary jca;
    private String varname = "nico:test1";
    private edu.gemini.epics.api.Channel ch;

    @Before
    public void setUp() {
        jca = JCALibrary.getInstance();
        cas = new ChannelAccessServerImpl();
        try {
            cas.start();
            ch = cas.createChannel(varname, 1);
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }

    }

    /**
     * Runs the server indefinitely, creates one channel and writes to it as fast as possible.
     */
    @Test
    public void testStressPut() {
        try {

            long first = System.currentTimeMillis();
            long last = first;
            final int iters = 100000000;//total number of iterations
            final int stepiters = 1000000;//iteration interval to sleep and print info
            int i = 0;
            int count = 1;
            try {

                for (i = 0; i < iters; i++) {
                    ch.setValue(i);
                    Thread.yield();
                    //if (i % 1000 == 0) Thread.sleep(10);
                    if (i % stepiters == 0) {
                        System.gc();
                        Thread.sleep(10);
                        long now = System.currentTimeMillis();
                        long elapsedtotal = now - first;
                        long elapsedlast = now - last;
                        last = now;
                        Double ratetotal = new Double((double) i * count / elapsedtotal * 1000.0);
                        Double ratelast = new Double((double) stepiters / elapsedlast * 1000.0);
                        long freemem = Runtime.getRuntime().freeMemory();
                        long totalmem = Runtime.getRuntime().totalMemory();
                        long usedmem = totalmem - freemem;
                        LOG.info("Count:" + count +
                                " Iteration:" + i +
                                ", Total Rate:" + ratetotal.intValue() +
                                "[updates/s], Last Rate: " + ratelast.intValue() +
                                "[updates/s], Used mem:" + usedmem / 1024 +
                                "[KB]");


                    }
                    //uncomment for continuous running
//                    if (i == (iters - 1)) {
//                        i = 0;
//                        count++;
//                    }

                }
            } catch (OutOfMemoryError ex) {
                System.out.println("Iter:" + i + "->" + ex.getMessage());
                ex.printStackTrace();
                fail();
            }


            long after = System.currentTimeMillis();
            long elapsed = after - first;
            double rate = (double) iters / elapsed * 1000.0;
            LOG.info("Time: " + elapsed + "[ms], rate: " + rate + "[updates/s]");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }


    }

    @After
    public void tearDown() {
        try {
            cas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }

    }
}

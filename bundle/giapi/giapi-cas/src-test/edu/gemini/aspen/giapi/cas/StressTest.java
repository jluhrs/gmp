package edu.gemini.aspen.giapi.cas;

import com.cosylab.epics.caj.CAJChannel;
import com.cosylab.epics.caj.CAJContext;
import gov.aps.jca.*;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_Int;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class StressTest
 *
 * @author Nicolas A. Barriga
 *         Date: Sep 30, 2010
 */
public class StressTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(StressTest.class.getName());
    private GiapiCas giapicas;
    private JCALibrary jca;


    @Before
    public void setUp() {
        jca = JCALibrary.getInstance();
        giapicas = new GiapiCas();
        try {
            giapicas.start();
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }
        giapicas.addVariable("nico:test1", DBR_Int.TYPE, new int[]{-1});
    }

    /**
     * Runs the server indefinitely, creates one channel and writes to it as fast as possible.
     */
    @Test
    public void testStressPut() {
        try {


           Context ctxt = giapicas.jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            Channel channel = ctxt.createChannel("nico:test1");

            ctxt.pendIO(1);
            long first=System.currentTimeMillis();
            long last=first;
            final int iters = Integer.MAX_VALUE;
            final int stepiters=1000000;
            int i=0;
	        int count =1;
            try {

                for (i = 0; i < iters; i++) {

                    channel.put(i);
                    //((CAJContext)ctxt).incrementPendingRequests();
                    channel.getContext().pendIO(1);
                   // channel.get();
                    //channel.getContext().pendIO(1);
                    //((CAJChannel)channel).getTransport().flush();
                    Thread.yield();
		            if(i%1000==0)Thread.sleep(10);
                    if (i % stepiters == 0) {
			            System.gc();
			            Thread.sleep(1);
                        long now = System.currentTimeMillis();
                        long elapsedtotal = now - first;
                        long elapsedlast = now-last;
                        last=now;
                        Double ratetotal = new Double((double)i*count / elapsedtotal * 1000.0);
                        Double ratelast = new Double((double) stepiters / elapsedlast * 1000.0);
                         long freemem=Runtime.getRuntime().freeMemory();
                        long totalmem=Runtime.getRuntime().totalMemory();
                        long usedmem=totalmem-freemem;
                        LOG.info("Count:" + count + 
				" Iteration:" + i +
                                ", Total Rate:" + ratetotal.intValue() +
                                "[updates/s], Last Rate: " + ratelast.intValue() +
                                "[updates/s], Used mem:"+usedmem/1024+
                                "[KB]");


                    }
                    if(i==(iters-1)){
                        i=0;
                        count++;
                    }

                }
            } catch (OutOfMemoryError ex) {
                System.out.println("Iter:" + i + "->" + ex.getMessage());
                ex.printStackTrace();
                //System.exit(2);
                fail();
            }
            //Thread.sleep(30000);

            channel.destroy();
            ctxt.pendIO(1);
            ctxt.destroy();
            long after = System.currentTimeMillis();
            long elapsed=after-first;
            double rate= (double)iters/elapsed*1000.0;
            LOG.info("Time: "+elapsed+"[ms], rate: "+rate+"[updates/s]");

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }


    }

    @After
    public void tearDown() {
        try {
            giapicas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }

    }
}

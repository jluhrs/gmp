package edu.gemini.cas;

import com.cosylab.epics.caj.cas.util.DefaultServerImpl;
import gov.aps.jca.*;
import gov.aps.jca.cas.ServerContext;
import gov.aps.jca.dbr.DBR;
import gov.aps.jca.dbr.DBR_Int;
import org.junit.Ignore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class TestJcas
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 29, 2010
 */
@Ignore
public class TestJcas {
    private static final Logger LOG = Logger.getLogger(TestJcas.class.getName());
    private DefaultServerImpl server;
    private ServerContext serverContext =null;
    private Context clientContext =null;
    private JCALibrary jca;
    private ExecutorService executor;
    private gov.aps.jca.Channel ch;

    public TestJcas() {
        this.jca = JCALibrary.getInstance();
        executor = Executors.newCachedThreadPool();
        server = new DefaultServerImpl();
    }

    public void start() {
        try {
            serverContext = jca.createServerContext(JCALibrary.CHANNEL_ACCESS_SERVER_JAVA, server);
            serverContext.printInfo();
            executor.execute(new Runnable() {
                public void run() {
                    LOG.info("Starting thread");
                    try {
                        serverContext.run(0);
                    } catch (Exception ex) {
                        LOG.log(Level.SEVERE, ex.getMessage(), ex);
                    }
                }
            });

            try {
                //TODO: this is UGLY!! need a way to see that the server is up and running
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
            clientContext = jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
            clientContext.printInfo();
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }


        server.createMemoryProcessVariable("test", DBR_Int.TYPE, new int[]{-2});
        try {
            //Just a "debugging" sleep, should go away
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

     executor.execute(new Runnable() {
                public void run() {

        try {
            ch = clientContext.createChannel("test");
            clientContext.pendIO(5);
            ch.printInfo();

        } catch (TimeoutException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        try {
            DBR dbr = ch.get();
            ch.getContext().pendIO(1);
            dbr.printInfo();
            int num = dbr.getCount();
            if (1 != num) {
                LOG.severe("Not the expected amount of values");
            }
            Object obj = dbr.getValue();
            int[] objarr = (int[]) obj;
            if (-2 != objarr[0]) {
                LOG.severe("Value different");
            }
        } catch (TimeoutException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (CAException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        LOG.info("Test OK!");
    }});
    }
    public void stop() {
         try {
             ch.destroy();
             serverContext.destroy();
             clientContext.destroy();
             executor.shutdown();
         } catch (CAException ex) {
             LOG.log(Level.SEVERE, ex.getMessage(), ex);
         }

     }

    public static void main(String args[]) {
        TestJcas jt = new TestJcas();
        jt.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
      //  jt.test();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
        jt.stop();
    }
}

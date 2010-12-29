package edu.gemini.aspen.gmp.heartbeat;


import edu.gemini.aspen.gmp.heartbeat.jms.HeartbeatConsumer;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * Class HeartbeatTest
 *
 * @author Nicolas A. Barriga
 *         Date: 12/29/10
 */
public class HeartbeatTest{
    @Test
    public void testBasic(){
        long secs=3;
        Heartbeat hb = new Heartbeat();
        HeartbeatConsumer hbc = new HeartbeatConsumer();
        hbc.start();
        hb.start();
        try{
            Thread.sleep(secs*1000);
            hb.stop();
            Thread.sleep(100);
            hbc.stop();
            assertEquals(true,hbc.getLast()>=(secs-1));
        }catch(InterruptedException ex){
            System.out.println(ex.getMessage());
            fail();
        }
    }
}

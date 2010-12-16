package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.gmp.statusservice.osgi.Channels;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.*;

/**
 * Class EpicsStatusServiceTest
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusServiceTest extends TestCase {

    @Test
    public void testBasic(){
        EpicsStatusService ess=new EpicsStatusService(null);
        List<Channels.ChannelConfig> lst= new ArrayList<Channels.ChannelConfig>();
        Channels.ChannelConfig ch= new Channels.ChannelConfig();
        ch.setGiapiname("name");
        ch.setEpicsname("other name");
        ch.setType("INT");
        ch.setInitial("3");
        lst.add(ch);
        try{
            ess.testInitialize(lst);
        }catch(IllegalStateException ex){

        }catch(Exception ex){
            fail();
        }
        Set<String> testSet= new HashSet<String>();
        testSet.add("name");
        assertEquals(testSet,ess.getAll().keySet());
        try{
            ess.shutdown();
        }catch(IllegalStateException ex){

        }catch(Exception ex){
            fail();
        }
        assertEquals(ess.getAll().keySet(),new HashSet<String>());
    }
}

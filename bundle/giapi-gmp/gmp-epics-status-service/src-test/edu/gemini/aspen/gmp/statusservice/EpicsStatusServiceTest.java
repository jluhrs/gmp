package edu.gemini.aspen.gmp.statusservice;


import junit.framework.TestCase;
import gov.aps.jca.dbr.DBRType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
        try{
            ess.addVariable("giapiname", "epicsname", DBRType.INT, new int[]{2});
        }catch(IllegalStateException ex){

        }catch(Exception ex){
            fail();
        }
        Map<String,String> testMap= new HashMap<String,String>();
        testMap.put("giapiname","epicsname");
        assertEquals(ess.getAll(),testMap);
        try{
            ess.removeVariable("giapiname");
        }catch(IllegalStateException ex){

        }catch(Exception ex){
            fail();
        }
        assertEquals(ess.getAll(),new HashMap<String,String>());
    }
}

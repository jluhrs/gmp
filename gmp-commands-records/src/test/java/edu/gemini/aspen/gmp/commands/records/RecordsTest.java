package edu.gemini.aspen.gmp.commands.records;

import edu.gemini.cas.Channel;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import gov.aps.jca.CAException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class RecordsTest
 *
 * @author Nicolas A. Barriga
 *         Date: 3/22/11
 */
public class RecordsTest {
    private ChannelAccessServerImpl cas;
    private final String carPrefix =  "gpitest:applyC";
    private final String prefix = "gpitest:";
    private final String cadName = "observe";
    private final String applyName = "apply";


    @Before
    public void setup() throws CAException {
        cas = new ChannelAccessServerImpl();
        cas.start();
    }

    @After
    public void tearDown() throws CAException {
        cas.stop();
    }

    @Test
    public void CARTest() throws CAException {
        CARRecord car = new CARRecord(cas, carPrefix);
        car.start();

        Channel<CARRecord.Val> val = cas.createChannel(carPrefix+".VAL",CARRecord.Val.UNKNOWN);
        Channel<String> omss = cas.createChannel(carPrefix+".OMSS","");
        Channel<Integer> oerr = cas.createChannel(carPrefix+".OERR",0);
        Channel<Integer> clid = cas.createChannel(carPrefix+".CLID",0);

        car.changeState(CARRecord.Val.BUSY, "a",-1,1);
        assertEquals(CARRecord.Val.BUSY, val.getVal().get(0));
        assertEquals("a",omss.getVal().get(0));
        assertEquals(new Integer(-1),oerr.getVal().get(0));
        assertEquals(new Integer(1),clid.getVal().get(0));

        car.stop();

        try{
            val.getVal();
            fail();
        }catch(IllegalStateException e){
            //ok
        }

    }

    @Test
    public void CADTest(){
        CADRecordImpl cad = new CADRecordImpl(cas,prefix,cadName,3);
        cad.start();

        //test different transitions
        //test that car goes to busy, then to idle

        cad.stop();

    }

    @Test
    public void applyTest(){
        ApplyRecord apply = new ApplyRecord(cas,prefix,applyName);
        apply.start();
        //test transitions and cad state changes

        apply.stop();
    }
}

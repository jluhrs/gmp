package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.status.*;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusservice.generated.*;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.IAlarmChannel;
import edu.gemini.cas.IChannel;
import edu.gemini.cas.IChannelAccessServer;
import gov.aps.jca.dbr.DBR_STS_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;

/**
 * Class EpicsStatusServiceTest
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusServiceTest extends TestCase {
    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceTest.class.getName());
    @Test
    public void testBasic(){
        IChannelAccessServer cas= mock(ChannelAccessServer.class);
        IAlarmChannel ich=mock(IAlarmChannel.class);
        when(cas.createIntegerAlarmChannel(anyString(),anyInt())).thenReturn(ich);
        EpicsStatusService ess=new EpicsStatusService(cas);
        LOG.info("Service name: "+ess.getName());

        Channels lst= new Channels();
        AlarmChannelType ch= new AlarmChannelType();
        ch.setGiapiname("name");
        ch.setEpicsname("other name");
        ch.setType(DataType.INT);
        ch.setInitial("3");
        lst.getSimpleChannelOrAlarmChannelOrHealthChannel().add(ch);


        ess.initialize(lst);

        Set<String> testSet = new HashSet<String>();
        testSet.add("name");
        assertEquals(testSet, ess.getAlarmChannels().keySet());


        ess.shutdown();

        assertEquals(ess.getChannels().keySet(),new HashSet<String>());
    }
}

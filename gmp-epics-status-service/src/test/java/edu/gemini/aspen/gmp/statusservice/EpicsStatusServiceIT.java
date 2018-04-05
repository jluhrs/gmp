package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.status.AlarmCause;
import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusservice.generated.AlarmChannelType;
import edu.gemini.aspen.gmp.statusservice.generated.BaseChannelType;
import edu.gemini.aspen.gmp.statusservice.generated.HealthChannelType;
import edu.gemini.aspen.gmp.statusservice.generated.SimpleChannelType;
import edu.gemini.gmp.top.Top;
import edu.gemini.gmp.top.TopImpl;
import edu.gemini.cas.AlarmChannel;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.dbr.DBR_STS_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Class EpicsStatusServiceTest
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusServiceIT{
    @Test
    public void testWithCas() throws Exception {
        //create fake config files
        File xml = File.createTempFile("EpicsTest", "xml");

        FileWriter xmlWrt = new FileWriter(xml);

        xmlWrt.write(EpicsStatusServiceConfigurationTest.xmlStr);
        xmlWrt.close();

        Top epicsTop = new TopImpl("gpi","gpi");

        //initialize and check channels are created
        EpicsStatusServiceConfiguration essc = new EpicsStatusServiceConfiguration(xml.getPath());

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        EpicsStatusService ess = new EpicsStatusService(cas, epicsTop, xml.getPath());

        ess.initialize();

        Map<String, AlarmChannel<?>> ac = ess.getAlarmChannels();

        Map<String, Channel<?>> nc = ess.getChannels();

        Map<String, Channel<String>> hc = ess.getHealthChannels();

        for (BaseChannelType cc : essc.getSimulatedChannels().getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            if (cc instanceof HealthChannelType) {
                assertTrue(hc.keySet().contains(epicsTop.buildStatusItemName(cc.getGiapiname())));
            } else if (cc instanceof AlarmChannelType) {
                assertTrue(ac.keySet().contains(epicsTop.buildStatusItemName(cc.getGiapiname())));
            } else if (cc instanceof SimpleChannelType) {
                assertTrue(nc.keySet().contains(cc.getGiapiname()));
            } else {
                fail();
            }
        }

        //update channels
        StatusItem<Integer> si = new BasicStatus<Integer>("giapinameint", 1);
        ess.update(si);
        assertEquals(1, ((int[]) nc.get("giapinameint").getDBR().getValue())[0]);

        StatusItem<Double> asi = new AlarmStatus<Double>(epicsTop.buildStatusItemName("giapialarmdouble"), 2.0, AlarmSeverity.ALARM_FAILURE, AlarmCause.ALARM_CAUSE_HI);
        ess.update(asi);
        assertEquals(2.0, ((double[]) ac.get(epicsTop.buildStatusItemName("giapialarmdouble")).getDBR().getValue())[0],0.00001);
        assertEquals(Severity.MAJOR_ALARM, ((DBR_STS_Double) ac.get(epicsTop.buildStatusItemName("giapialarmdouble")).getDBR()).getSeverity());
        assertEquals(Status.HIGH_ALARM, ((DBR_STS_Double) ac.get(epicsTop.buildStatusItemName("giapialarmdouble")).getDBR()).getStatus());

        assertEquals(Health.BAD.name(), ((String[]) hc.get(epicsTop.buildStatusItemName("giapihealth1")).getDBR().getValue())[0]);
        StatusItem<Health> hsi = new HealthStatus(epicsTop.buildStatusItemName("giapihealth1"), Health.GOOD);
        ess.update(hsi);
        assertEquals(Health.GOOD.name(), ((String[]) hc.get(epicsTop.buildStatusItemName("giapihealth1")).getDBR().getValue())[0]);
        hsi = new HealthStatus(epicsTop.buildStatusItemName("giapihealth1"), Health.WARNING);
        ess.update(hsi);
        assertEquals(Health.WARNING.name(), ((String[]) hc.get(epicsTop.buildStatusItemName("giapihealth1")).getDBR().getValue())[0]);

        //shutdown and check channels get deleted
        ess.shutdown();

        assertEquals(ess.getAlarmChannels().keySet(), new HashSet<String>());
        assertEquals(ess.getChannels().keySet(), new HashSet<String>());


        cas.stop();
    }
}

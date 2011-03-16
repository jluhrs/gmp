package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.status.AlarmCause;
import edu.gemini.aspen.giapi.status.AlarmSeverity;
import edu.gemini.aspen.giapi.status.Health;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusservice.generated.*;
import edu.gemini.cas.Channel;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.cas.AlarmChannel;
import gov.aps.jca.dbr.DBR_STS_Double;
import gov.aps.jca.dbr.Severity;
import gov.aps.jca.dbr.Status;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Class EpicsStatusServiceTest
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsStatusServiceIT extends TestCase {
    private static final Logger LOG = Logger.getLogger(EpicsStatusServiceIT.class.getName());

    @Test
    public void testWithCas() throws Exception{
        //create fake config files
        File xml = null;

        xml = File.createTempFile("EpicsTest", "xml");

        File xsd = null;
        xsd = File.createTempFile("EpicsTest", "xsd");

        FileWriter xmlWrt = new FileWriter(xml);
        FileWriter xsdWrt = new FileWriter(xsd);

        xmlWrt.write(EpicsStatusServiceConfigurationTest.xmlStr);
        xsdWrt.write(EpicsStatusServiceConfigurationTest.xsdStr);
        xmlWrt.close();
        xsdWrt.close();


        //initialize and check channels are created
        EpicsStatusServiceConfiguration essc = new EpicsStatusServiceConfiguration(xml.getPath(), xsd.getPath());

        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        EpicsStatusService ess = new EpicsStatusService(cas);


        ess.initialize(essc.getSimulatedChannels());

        Map<String, AlarmChannel<?>> ac = ess.getAlarmChannels();

        Map<String, Channel<?>> nc = ess.getChannels();

        Map<String, Channel<String>> hc = ess.getHealthChannels();

        for (BaseChannelType cc : essc.getSimulatedChannels().getSimpleChannelOrAlarmChannelOrHealthChannel()) {
            if (cc instanceof HealthChannelType) {
                assertTrue(hc.keySet().contains(cc.getGiapiname()));
            } else if (cc instanceof AlarmChannelType) {
                assertTrue(ac.keySet().contains(cc.getGiapiname()));
            } else if (cc instanceof SimpleChannelType) {
                assertTrue(nc.keySet().contains(cc.getGiapiname()));
            } else {
                fail();
            }
        }

        //update channels
        StatusItem<Integer> si = new BasicStatus<Integer>("giapinameint", 1);
        ess.update(si);
        assertEquals(1, ((int[]) nc.get("giapinameint").getValue().getValue())[0]);

        StatusItem<Double> asi = new AlarmStatus<Double>("giapialarmdouble", 2.0, AlarmSeverity.ALARM_FAILURE, AlarmCause.ALARM_CAUSE_HI);
        ess.update(asi);
        assertEquals(2.0, ((double[]) ac.get("giapialarmdouble").getValue().getValue())[0]);
        assertEquals(Severity.MAJOR_ALARM, ((DBR_STS_Double) ac.get("giapialarmdouble").getValue()).getSeverity());
        assertEquals(Status.HIGH_ALARM, ((DBR_STS_Double) ac.get("giapialarmdouble").getValue()).getStatus());


        assertEquals(Health.BAD.name(), ((String[]) hc.get("giapihealth1").getValue().getValue())[0]);
        StatusItem<Health> hsi = new HealthStatus("giapihealth1", Health.GOOD);
        ess.update(hsi);
        assertEquals(Health.GOOD.name(), ((String[]) hc.get("giapihealth1").getValue().getValue())[0]);
        hsi = new HealthStatus("giapihealth1", Health.WARNING);
        ess.update(hsi);
        assertEquals(Health.WARNING.name(), ((String[]) hc.get("giapihealth1").getValue().getValue())[0]);

        //shutdown and check channels get deleted
        ess.shutdown();

        assertEquals(ess.getAlarmChannels().keySet(), new HashSet<String>());
        assertEquals(ess.getChannels().keySet(), new HashSet<String>());


        cas.stop();
    }
}

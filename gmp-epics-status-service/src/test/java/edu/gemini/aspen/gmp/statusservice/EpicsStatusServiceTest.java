package edu.gemini.aspen.gmp.statusservice;


import edu.gemini.aspen.giapi.status.*;
import edu.gemini.aspen.giapi.status.impl.AlarmStatus;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import edu.gemini.aspen.giapi.status.impl.HealthStatus;
import edu.gemini.aspen.gmp.statusservice.generated.*;
import edu.gemini.cas.ChannelAccessServer;
import edu.gemini.cas.IAlarmChannel;
import edu.gemini.cas.IChannel;
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
        EpicsStatusService ess=new EpicsStatusService(null);
        LOG.info("Service name: "+ess.getName());
        Channels lst= new Channels();
        AlarmChannelType ch= new AlarmChannelType();
        ch.setGiapiname("name");
        ch.setEpicsname("other name");
        ch.setType(DataType.INT);
        ch.setInitial("3");
        lst.getSimpleChannelOrAlarmChannelOrHealthChannel().add(ch);
        try{
            ess.testInitialize(lst);
        }catch(IllegalStateException ex){
            fail();
        }catch(Exception ex){
            fail();
        }
        Set<String> testSet= new HashSet<String>();
        testSet.add("name");
        assertEquals(testSet,ess.getAlarmChannels().keySet());

        //just for test coverage :)
        ess.dump();

        try{
            ess.shutdown();
        }catch(IllegalStateException ex){
            fail();
        }catch(Exception ex){
            fail();
        }
        assertEquals(ess.getChannels().keySet(),new HashSet<String>());
    }

    @Test
    public void testWithCas(){
        try{
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
            EpicsStatusServiceConfiguration essc=new EpicsStatusServiceConfiguration(xml.getPath(), xsd.getPath());

            ChannelAccessServer cas = new ChannelAccessServer();
            cas.start();
            EpicsStatusService ess = new EpicsStatusService(cas);

            try {
                ess.initialize(essc.getSimulatedChannels());
            } catch (IllegalStateException ex) {
                fail();
            } catch (Exception ex) {
                fail();
            }
            Map<String,IAlarmChannel> ac= ess.getAlarmChannels();

            Map<String,IChannel> nc= ess.getChannels();

            Map<String,IChannel> hc= ess.getHealthChannels();

             for(BaseChannelType cc:essc.getSimulatedChannels().getSimpleChannelOrAlarmChannelOrHealthChannel()){
                 if(cc instanceof HealthChannelType){
                    assertTrue(hc.keySet().contains(cc.getGiapiname()));
                 }else if(cc instanceof AlarmChannelType){
                    assertTrue(ac.keySet().contains(cc.getGiapiname()));
                 }else if (cc instanceof SimpleChannelType){
                    assertTrue(nc.keySet().contains(cc.getGiapiname()));
                 }else{
                     fail();
                 }
            }

            //update channels
            StatusItem<Integer> si=new BasicStatus<Integer>("giapinameint",1);
            ess.update(si);
            assertEquals(1, ((int[])nc.get("giapinameint").getValue().getValue())[0]);

            StatusItem<Double> asi=new AlarmStatus<Double>("giapialarmdouble",2.0, AlarmSeverity.ALARM_FAILURE,AlarmCause.ALARM_CAUSE_HI);
            ess.update(asi);
            assertEquals(2.0, ((double[])ac.get("giapialarmdouble").getValue().getValue())[0]);
            assertEquals(Severity.MAJOR_ALARM, ((DBR_STS_Double) ac.get("giapialarmdouble").getValue()).getSeverity());
            assertEquals(Status.HIGH_ALARM,((DBR_STS_Double) ac.get("giapialarmdouble").getValue()).getStatus());


            assertEquals(Health.BAD.name(), ((String[])hc.get("giapihealth1").getValue().getValue())[0]);
            StatusItem<Health> hsi=new HealthStatus("giapihealth1",Health.GOOD);
            ess.update(hsi);
            assertEquals(Health.GOOD.name(), ((String[])hc.get("giapihealth1").getValue().getValue())[0]);
            hsi=new HealthStatus("giapihealth1",Health.WARNING);
            ess.update(hsi);
            assertEquals(Health.WARNING.name(), ((String[])hc.get("giapihealth1").getValue().getValue())[0]);

            //shutdown and check channels get deleted
            try {
                ess.shutdown();
            } catch (IllegalStateException ex) {
                fail();
            } catch (Exception ex) {
                fail();
            }
            assertEquals(ess.getAlarmChannels().keySet(), new HashSet<String>());
            assertEquals(ess.getChannels().keySet(), new HashSet<String>());


            cas.stop();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            fail();
        }
    }
}

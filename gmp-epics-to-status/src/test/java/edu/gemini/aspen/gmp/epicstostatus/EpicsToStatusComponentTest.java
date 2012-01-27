package edu.gemini.aspen.gmp.epicstostatus;


import edu.gemini.aspen.giapi.status.StatusHandler;
import edu.gemini.aspen.giapi.status.StatusItem;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregate;
import edu.gemini.aspen.giapi.statusservice.StatusHandlerAggregateImpl;
import edu.gemini.aspen.giapi.statusservice.StatusService;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.EpicsService;
import edu.gemini.epics.NewEpicsReader;
import edu.gemini.epics.NewEpicsWriter;
import edu.gemini.epics.ReadWriteClientEpicsChannel;
import edu.gemini.epics.api.Channel;
import edu.gemini.epics.impl.NewEpicsReaderImpl;
import edu.gemini.epics.impl.NewEpicsWriterImpl;
import edu.gemini.jms.activemq.provider.ActiveMQJmsProvider;
import edu.gemini.jms.api.JmsProvider;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Class EpicsStatusServiceTest
 *
 * @author Nicolas A. Barriga
 *         Date: Nov 9, 2010
 */
public class EpicsToStatusComponentTest {
    private static final Logger LOG = Logger.getLogger(EpicsToStatusComponentTest.class.getName());

    private class StatusMonitor implements StatusHandler {
        private StatusItem lastItem;

        @Override
        public String getName() {
            return "Status Monitor";
        }

        @Override
        public <T> void update(StatusItem<T> item) {
            lastItem = item;
        }

        public StatusItem getLast() {
            return lastItem;
        }
    }

    static {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");
    }

    @Test
    public void testWithCas() throws Exception {
        //create fake config files
        File xml = null;

        xml = File.createTempFile("EpicsTest", "xml");

        File xsd = null;
        xsd = File.createTempFile("EpicsTest", "xsd");

        FileWriter xmlWrt = new FileWriter(xml);
        FileWriter xsdWrt = new FileWriter(xsd);

        xmlWrt.write(EpicsToStatusConfigurationTest.xmlStr);
        xsdWrt.write(EpicsToStatusConfigurationTest.xsdStr);
        xmlWrt.close();
        xsdWrt.close();


        //read config
        EpicsToStatusConfiguration essc = new EpicsToStatusConfiguration(xml.getPath(), xsd.getPath());

        //create EPICS channels
        ChannelAccessServerImpl cas = new ChannelAccessServerImpl();
        cas.start();
        Channel<Integer> casCh = cas.createChannel("giapitest:epicschannelint", 1);
        cas.createAlarmChannel("giapitest:epicsalarmfloat", 1.0f);
        cas.createChannel("giapitest:epicshealth1", "BAD");
        Thread.sleep(100);


        //Create EPICS reader and writer
        JmsProvider provider = new ActiveMQJmsProvider("vm://EpicsToStatusComponentTestBroker");
        EpicsService epicsServ = new EpicsService("127.0.0.1");
        epicsServ.startService();
        NewEpicsReader reader = new NewEpicsReaderImpl(epicsServ);
        NewEpicsWriter writer = new NewEpicsWriterImpl(epicsServ);


        //setup a status service and a status handler to check updates are getting published
        StatusMonitor monitor = new StatusMonitor();
        StatusHandlerAggregate aggregate = new StatusHandlerAggregateImpl();
        aggregate.bindStatusHandler(monitor);
        StatusService service = new StatusService(aggregate, provider, "Status Monitor Service Client", "giapitest:statusitemint");
        service.initialize();

        //initialize client EPICS channels, channel listeners and StatusSetters
        EpicsToStatusComponent esc = new EpicsToStatusComponent(reader, provider, xml.getPath(), xsd.getPath());
        esc.initialize();

        //write the channel to trigger an update
        ReadWriteClientEpicsChannel<Integer> writeCh = writer.getIntegerChannel("giapitest:epicschannelint");
        assertTrue(writeCh.isValid());
        writeCh.setValue(2);

        //wait a bit and check that the update got published
        Thread.sleep(100);
        assertEquals("giapitest:statusitemint", monitor.getLast().getName());
        assertEquals(2, monitor.getLast().getValue());


        //shutdown everything
        esc.shutdown();
        epicsServ.stopService();
        service.stopComponent();
        cas.stop();
    }
}

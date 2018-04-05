package edu.gemini.aspen.integrationtests;

import com.cosylab.epics.caj.CAJContext;
import edu.gemini.cas.impl.ChannelAccessServerImpl;
import edu.gemini.epics.api.Channel;
import gov.aps.jca.CAException;
import gov.aps.jca.JCALibrary;
import org.junit.After;
import org.junit.Before;

/**
 * Class NewEpicsTestBase
 *
 * @author Nicolas A. Barriga
 *         Date: 11/9/11
 */
public class NewEpicsTestBase {
    CAJContext context;
    ChannelAccessServerImpl cas;
    final String doubleName = "giapitest:double";
    final String floatName = "giapitest:float";
    final String intName = "giapitest:int";
    final String stringName = "giapitest:string";
    Channel<Double> doubleChannel;
    Channel<Integer> intChannel;
    Channel<Float> floatChannel;
    Channel<String> stringChannel;

    static {
        System.setProperty("com.cosylab.epics.caj.CAJContext.addr_list", "127.0.0.1");
        System.setProperty("com.cosylab.epics.caj.CAJContext.auto_addr_list", "false");
    }

    @Before
    public void setup() throws CAException {
        JCALibrary jca = JCALibrary.getInstance();
        context = (CAJContext) jca.createContext(JCALibrary.CHANNEL_ACCESS_JAVA);
        cas = new ChannelAccessServerImpl();
        cas.start();
        doubleChannel = cas.createChannel(doubleName, 1.0);
        intChannel = cas.createChannel(intName, 1);
        floatChannel = cas.createChannel(floatName, 1.0f);
        stringChannel = cas.createChannel(stringName, "1");
    }

    @After
    public void tearDown() throws CAException {
        cas.destroyChannel(doubleChannel);
        cas.destroyChannel(intChannel);
        cas.destroyChannel(floatChannel);
        cas.destroyChannel(stringChannel);
        cas.stop();
        context.destroy();
    }
}

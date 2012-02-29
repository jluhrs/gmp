package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.gmp.services.PropertyHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

/**
 * Class SimplePropertyHolderIT
 *
 * @author Nicolas A. Barriga
 *         Date: 8/31/11
 */
@RunWith(JUnit4TestRunner.class)
public class SimplePropertyHolderIT extends FelixContainerConfigurationBase {
    @Configuration
    public static Option[] withJMSProviderConfig() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("gmp-services").groupId("edu.gemini.aspen.gmp").update().versionAsInProject()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/propertyholder";
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getBundle("edu.gemini.aspen.giapi"));
        assertNotNull(getBundle("com.springsource.javax.jms"));
        assertNotNull(getBundle("edu.gemini.jms-api"));
        assertNotNull(getBundle("edu.gemini.aspen.gmp.services"));
    }


    @Test
    public void testService() {
        Bundle bundle = getBundle("edu.gemini.aspen.gmp.services");
        assertNotNull(context.getServiceReference(PropertyHolder.class.getName()));
        assertNotNull(context.getServiceReference(ManagedService.class.getName()));
    }

    @Test
    public void testProperties() {
        assertEquals("localhost", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("GMP_HOST_NAME"));
        assertEquals("/tmp", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("DHS_ANCILLARY_DATA_PATH"));
        assertEquals("/tmp", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("DHS_SCIENCE_DATA_PATH"));
        assertEquals("/tmp/perm", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("DHS_PERMANENT_SCIENCE_DATA_PATH"));
        assertEquals("/tmp", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("DHS_INTERMEDIATE_DATA_PATH"));
        assertEquals("", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("DEFAULT"));
    }

    @Test
    public void testUpdateProperties() throws IOException, InvalidSyntaxException, InterruptedException {
        //use config admin to change properties and then check
        ConfigurationAdmin ca = (ConfigurationAdmin) context.getService(context.getServiceReference(ConfigurationAdmin.class.getName()));
        org.osgi.service.cm.Configuration config = ca.listConfigurations("(service.factorypid=edu.gemini.aspen.gmp.services.properties.SimplePropertyHolder)")[0];

        Dictionary props = config.getProperties();
        if (props == null) {
            props = new Hashtable();
        }
        // configure the Dictionary
        props.put("GMP_HOST_NAME", "bla");
        //push the configuration dictionary to the SmsService
        config.update(props);

        Thread.sleep(1000);
        assertEquals("bla", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("GMP_HOST_NAME"));

        //put the original back, just in case ;)
        props.put("GMP_HOST_NAME", "localhost");
        //push the configuration dictionary to the SmsService
        config.update(props);
        //sleep to allow time to write file before exiting
        Thread.sleep(1000);
        assertEquals("localhost", ((PropertyHolder) context.getService(context.getServiceReference(PropertyHolder.class.getName()))).getProperty("GMP_HOST_NAME"));

    }
}
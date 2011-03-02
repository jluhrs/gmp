package edu.gemini.aspen.giapi.status.dispatcher;

import edu.gemini.aspen.giapi.commands.ConfigPath;
import edu.gemini.aspen.giapi.status.*;
import edu.gemini.aspen.giapi.status.impl.BasicStatus;
//import edu.gemini.giapi.tool.status.StatusSetter;
import edu.gemini.jms.api.*;
import org.junit.*;
import org.junit.runner.*;
import org.ops4j.pax.exam.*;
import org.ops4j.pax.exam.junit.*;
import org.osgi.framework.*;

import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.*;

@Ignore
@RunWith(JUnit4TestRunner.class)
public class StatusDispatcherIT {
    private static final Logger LOG = Logger.getLogger(StatusDispatcherIT.class.getName());
    @Inject
    private int counter=0;
    private BundleContext context;
    private abstract class TestHandler implements FilteredStatusHandler{

            @Override
            public String getName() {
                return "Filter: "+getFilter().toString();
            }

            @Override
            public void update(StatusItem item) {
                //check that we only get items that are children of our filter
                assertTrue(item.getName().startsWith(getFilter().toString()));
                LOG.info("ITEM: "+item);
                counter++;
            }
    }
    @Configuration
    public static Option[] withStatusDbAndJMSProviderConfig() {
        return options(
                felix(),
                cleanCaches(),
                systemProperty("felix.fileinstall.dir").value(System.getProperty("basedir") + "/src/test/resources/conf/services"),
                systemProperty("felix.fileinstall.noInitialDelay").value("true"),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("giapi-jms-util").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0"),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").version("1.2.8"),
                mavenBundle().artifactId("org.apache.felix.fileinstall").groupId("org.apache.felix").version("3.1.10"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("guava").groupId("com.google.guava").version("8.0.0"),
                mavenBundle().artifactId("gmp-statusdb").groupId("edu.gemini.aspen.gmp").version("0.1.0"),
                mavenBundle().artifactId("pax-logging-api").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("pax-logging-service").groupId("org.ops4j.pax.logging").version("1.6.0"),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("giapi-status-service").groupId("edu.gemini.aspen").update().versionAsInProject(),
                mavenBundle().artifactId("giapi-status-dispatcher").groupId("edu.gemini.aspen").update().versionAsInProject()
                );
    }

   @Test
    public void bundleExistence() {
        assertNotNull(getStatusDispatcherBundle());

    }

    private Bundle getStatusDispatcherBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.aspen.giapi-status-dispatcher".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail();
        return null;
    }

    private boolean isStatusDispatcherRunning() {
        Bundle statusDispatcherBundle = getStatusDispatcherBundle();
        for (ServiceReference s : statusDispatcherBundle.getRegisteredServices()) {
            if (StatusDispatcher.class.getName().equals(s.getProperty("service.pid"))) {
                return true;
            }
        }
        return false;
    }


    private boolean isServiceClassInUse(Class<?> serviceClass) {
        ServiceReference aggregateReference = context.getServiceReference(serviceClass.getName());
        return aggregateReference != null;
    }

    private boolean isJMSProviderInUse() {
        return isServiceClassInUse(JmsProvider.class);
    }

    @Test
    public void withStatusDbAndJMSProviderStatusServiceShouldExist() {
        assertTrue("StatusDispatcher should exist as a managed service", isStatusDispatcherRunning());
        assertTrue("JMSProvider should be available", isJMSProviderInUse());
    }

    @Test
    public void testUpdate()throws Exception{
        ServiceReference ref = context.getServiceReference("StatusDispatcher");
        assertNotNull(ref);
        StatusDispatcher dispatcher = (StatusDispatcher)context.getService(ref);
        assertNotNull(dispatcher);
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:a:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        });
        FilteredStatusHandler h = new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b");
            }
        };
        dispatcher.bindStatusHandler(h);
        dispatcher.unbindStatusHandler(h);
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b:1");
            }
        });
        dispatcher.bindStatusHandler(new TestHandler() {
            @Override
            public ConfigPath getFilter() {
                return new ConfigPath("gpi:b:2");
            }
        });
//        StatusSetter ss=new StatusSetter("gpi:b:1");
//        ss.setStatusItem(new BasicStatus<String>("gpi:b:1","new value"));
//        assertEquals(4,counter);
    }

}

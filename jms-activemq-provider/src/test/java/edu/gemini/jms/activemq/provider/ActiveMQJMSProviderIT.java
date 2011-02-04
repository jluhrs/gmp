package edu.gemini.jms.activemq.provider;

import edu.gemini.jms.api.JmsProvider;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

/**
 * Basic integration test for JMS Provider basically verifying that the service starts and can be configured

 * For testing it uses pax-exam to create a felix based OSGi container.
 *
 * <a href="http://paxrunner.ops4j.org/display/paxexam/Advanced+JUnit+usage">Pax-examn</a>
 *
 * @cquiroz
 */
@RunWith(JUnit4TestRunner.class)
public class ActiveMQJMSProviderIT {
    @Inject
    private BundleContext context;

    @Configuration
    public static Option[] baseConfig() {
        return options(
                felix(),
                mavenBundle().artifactId("org.apache.felix.ipojo").groupId("org.apache.felix").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.jms").groupId("javax.jms").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").version("4.2.0"),
                //mavenBundle().artifactId("jms-activemq-broker").groupId("edu.gemini.jms").version("1.1.0"),
                mavenBundle().artifactId("activemq-core").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-j2ee-management_1.1_spec").groupId("org.apache.geronimo.specs").version("1.0.1"),
                mavenBundle().artifactId("kahadb").groupId("org.apache.activemq").version("5.4.2"),
                mavenBundle().artifactId("geronimo-annotation_1.0_spec").groupId("org.apache.geronimo.specs").version("1.1.1"),
                mavenBundle().artifactId("com.springsource.org.apache.commons.logging").groupId("org.apache.commons").version("1.1.1"),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("jms-activemq-provider").groupId("edu.gemini.jms").version("1.1.0").update(),
                mavenBundle().artifactId("org.apache.felix.configadmin").groupId("org.apache.felix").versionAsInProject()
        );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getJmsProviderBundle());
    }

    private Bundle getJmsProviderBundle() {
        for (Bundle b : context.getBundles()) {
            if ("edu.gemini.jms.activemq-provider".equals(b.getSymbolicName())) {
                return b;
            }
        }
        fail("Bundle not found");
        throw new AssertionError();
    }

    @Test
    @Ignore("Cannot find the service yet")
    public void doesServiceExist() throws IOException, BundleException, InterruptedException {
        // Wait a sec for the container to start
        TimeUnit.MILLISECONDS.wait(500);
        assertTrue(isJMSProviderInUse());
    }

    private boolean isServiceClassInUse(Class<?> serviceClass) {
        ServiceReference aggregateReference = context.getServiceReference(serviceClass.getName());
        return aggregateReference != null;
    }

    private boolean isJMSProviderInUse() {
        return isServiceClassInUse(JmsProvider.class);
    }

}

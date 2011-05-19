package edu.gemini.aspen.integrationtests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

@RunWith(JUnit4TestRunner.class)
public class GDSEndToEndIT extends FelixContainerConfigurationBase {
    private final Logger LOG = Logger.getLogger(HeartbeatDistributorIT.class.getName());

    @Configuration
    public static Option[] withJMSProviderConfig() {
        return options(
                mavenBundle().artifactId("file-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("fits-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("scala-library").groupId("com.weiglewilczek.scala-lang-osgi").versionAsInProject(),
                mavenBundle().artifactId("gds-api").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getBundle("edu.gemini.aspen.gds.api"));
    }

}

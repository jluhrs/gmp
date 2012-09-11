package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.gds.api.CompositePostProcessingPolicy;
import edu.gemini.aspen.gds.api.KeywordActorsFactory;
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService;
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;

@RunWith(JUnit4TestRunner.class)
public class GDSWithODBIT extends GDSIntegrationBase {

    @Configuration
    public static Option[] gdsODBBundles() {
        return options(
                systemProperty("jini.lus.import.hosts").value("gsodbtest.cl.gemini.edu"),
                systemProperty("jini.lus.import.groups").value("test"),
                systemProperty("org.osgi.framework.system.packages.extra").value("sun.misc,sun.security.action,sun.rmi.runtime,edu.gemini.rmi.server"),
                systemProperty("org.osgi.framework.bootdelegation").value("java.rmi.server"),
                mavenBundle().artifactId("pax-web-jetty-bundle").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-extender-whiteboard").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-spi").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("spdb-activator").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("rr").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("gds-odb-actors").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-postprocessing-policy").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("jini-driver").groupId("gemini-nocs").versionAsInProject()
        );
    }

    @Test
    public void bundleExistence() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(400);
        assertNotNull(getBundle("edu.gemini.jini.jini-driver"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.odb"));
    }

    @Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException {
        TimeUnit.MILLISECONDS.sleep(800);

        assertNotNull(context.getService(context.getServiceReference(GDSConfigurationService.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositePostProcessingPolicy.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStatePublisher.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateRegistrar.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(KeywordActorsFactory.class.getName())));
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        postProgramIDToDataLabelLink();

        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        assertFalse(originalKeywords.contains("PIFSTNAM"));

        sendObservationEvents(eventHandler, new DataLabel("sample1.fits"));
        TimeUnit.MILLISECONDS.sleep(2000);

        File finalFile = new File(FINAL_FITS_DIR + FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Set<String> afterProcessingKeywords = readFinalKeywords();

        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("PIFSTNAM"));
    }

    private void postProgramIDToDataLabelLink() {
        ProgramIdDatabase programIdDatabase = (ProgramIdDatabase) context.getService(context.getServiceReference(ProgramIdDatabase.class.getName()));
        assertNotNull(programIdDatabase);
        DataLabel dataLabel = new DataLabel("S20110427-01");
        programIdDatabase.store(dataLabel, "GS-2006B-Q-57");
    }

}

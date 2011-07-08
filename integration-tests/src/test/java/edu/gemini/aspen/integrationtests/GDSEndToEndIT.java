package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory;
import edu.gemini.aspen.gds.api.CompositeErrorPolicy;
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.fits.FitsParseException;
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class GDSEndToEndIT extends GDSIntegrationBase {
    @Configuration
    public static Option[] gdsEpicsBundles() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("jca-lib").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("gds-epics-actors").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Test
    public void bundleExistence() throws InterruptedException {
        assertNotNull(getBundle("edu.gemini.aspen.gds.api"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.keywords.database"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.actors"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.fits"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.epics"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.obsevent.handler"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.observationstate"));
    }

    @Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException, FitsParseException {
        TimeUnit.MILLISECONDS.sleep(400);
        assertNotNull(context.getService(context.getServiceReference(CompositeActorsFactory.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(KeywordsDatabase.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositeErrorPolicy.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStatePublisher.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateRegistrar.class.getName())));
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        sendObservationEvents(eventHandler);

        File finalFile = new File(FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Set<String> afterProcessingKeywords = readFinalKeywords();
        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("AIRMASS"));
        assertTrue(afterProcessingKeywords.contains("HUMIDITY"));
        assertTrue(afterProcessingKeywords.contains("TAMBIENT"));
        assertTrue(afterProcessingKeywords.contains("PRESSURE"));
        assertTrue(afterProcessingKeywords.contains("WINDSPEE"));
        assertTrue(afterProcessingKeywords.contains("WINDDIRE"));
    }

}

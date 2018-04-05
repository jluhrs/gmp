package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory;
import edu.gemini.aspen.gds.api.CompositePostProcessingPolicy;
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
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
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class GDSEndToEndStartedFromSeqexecIT extends GDSIntegrationBase {
    @Configuration
    public static Option[] gdsEpicsBundles() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("epics-api").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("caj").groupId("edu.gemini.external.osgi.com.cosylab.epics.caj").versionAsInProject(),
                mavenBundle().artifactId("jca").groupId("edu.gemini.external.osgi.gov.aps.jca").versionAsInProject(),
                mavenBundle().artifactId("gds-epics-actors").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Test
    public void simulateObservationEvent() throws InterruptedException, URISyntaxException, IOException {
        TimeUnit.MILLISECONDS.sleep(4000);
        assertNotNull(context.getService(context.getServiceReference(CompositeActorsFactory.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(KeywordsDatabase.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositePostProcessingPolicy.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStatePublisher.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateRegistrar.class.getName())));
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        DataLabel dataLabel = new DataLabel("sample1.fits");

        // Start transaction
        eventHandler.onObservationEvent(ObservationEvent.EXT_START_OBS, dataLabel);

        sendObservationEvents(eventHandler, dataLabel);
        TimeUnit.MILLISECONDS.sleep(2000);

        File finalFile = new File(FINAL_FITS_DIR + FINAL_FITS_FILE);

        assertFalse(finalFile.exists());

        // End transaction
        eventHandler.onObservationEvent(ObservationEvent.EXT_END_OBS, dataLabel);
        TimeUnit.MILLISECONDS.sleep(2000);

        assertTrue(finalFile.exists());

        Set<String> afterProcessingKeywords = readFinalKeywords();
        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("AIRMASS"));
        assertTrue(afterProcessingKeywords.contains("HUMIDITY"));
        assertTrue(afterProcessingKeywords.contains("TAMBIENT"));
        assertTrue(afterProcessingKeywords.contains("PRESSURE"));
        assertTrue(afterProcessingKeywords.contains("WINDDIRE"));
        assertTrue(afterProcessingKeywords.contains("WINDSPEE"));
    }

}
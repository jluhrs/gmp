package edu.gemini.aspen.integrationtests;

import com.google.common.collect.Sets;
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory;
import edu.gemini.aspen.gds.api.CompositePostProcessingPolicy;
import edu.gemini.aspen.gds.api.GDSConfiguration;
import edu.gemini.aspen.gds.api.PostProcessingPolicy;
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService;
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

//import edu.gemini.fits.FitsParseException;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class GDSEndToEndIT extends GDSIntegrationBase {
    @Configuration
    public Option[] gdsEpicsBundles() {
        return concatenate(super.gdsBundles(), options(
                mavenBundle().artifactId("epics-api").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("caj").groupId("edu.gemini.external.osgi.com.cosylab.epics.caj").versionAsInProject(),
                mavenBundle().artifactId("jca").groupId("edu.gemini.external.osgi.gov.aps.jca").versionAsInProject(),
                mavenBundle().artifactId("gds-epics-actors").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-postprocessing-policy").groupId("edu.gemini.aspen.gds").versionAsInProject()
        ));
    }

    @Test
    public void bundleExistence() {
        assertNotNull(getBundle("edu.gemini.aspen.gds.api"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.keywords.database"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.actors"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.fits"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.epics"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.obsevent.handler"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.observationstate"));
    }

    @Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException {
        TimeUnit.MILLISECONDS.sleep(4000);
        assertNotNull(context.getService(context.getServiceReference(GDSConfigurationService.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositeActorsFactory.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(KeywordsDatabase.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositePostProcessingPolicy.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateRegistrar.class.getName())));
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        //verify that the error policies are loaded
        Set<String> errorPolicies = Sets.newHashSet();
        try {
            for (ServiceReference ref : context.getServiceReferences(PostProcessingPolicy.class.getName(), null)) {
                errorPolicies.add(context.getService(ref).getClass().getName());
            }
        } catch (InvalidSyntaxException ex) {
            fail();
        }
        assertTrue(errorPolicies.contains("edu.gemini.aspen.gds.postprocessingpolicy.EnforceOrderPolicy"));


        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        sendObservationEvents(eventHandler, new DataLabel("sample1.fits"));
        TimeUnit.MILLISECONDS.sleep(2000);

        File finalFile = new File(FINAL_FITS_DIR + FINAL_FITS_FILE);
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
package edu.gemini.aspen.integrationtests;

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
public class GDSEndToEndWithExtensionsIT extends GDSIntegrationBase {
    @Configuration
    public static Option[] gdsEpicsBundles() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("jca-lib").groupId("edu.gemini.external.osgi.jca-lib").versionAsInProject(),
                mavenBundle().artifactId("gds-epics-actors").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/GDSEndToEndWithExtensionsIT";
    }

    @Test
    public void bundleExistence() throws InterruptedException {
        assertNotNull(getBundle("edu.gemini.aspen.gds.api"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.keywords.database"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.actors"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.fits"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.epics"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.obsevent.handler"));
    }

    // TODO: Use a file with extensions
    //@Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException, FitsParseException {
        TimeUnit.MILLISECONDS.sleep(400);
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        sendObservationEvents(eventHandler);

        File finalFile = new File(FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Set<String> afterProcessingKeywords = readFinalKeywords();
        System.out.println(afterProcessingKeywords);

        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("WINDSPEE"));
        assertTrue(afterProcessingKeywords.contains("WINDDIRE"));
    }

}

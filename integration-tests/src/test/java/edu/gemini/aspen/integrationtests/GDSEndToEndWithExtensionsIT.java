package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.fits.FitsParseException;
import edu.gemini.fits.Header;
import edu.gemini.fits.Hedit;
import org.junit.Before;
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
    protected static final String FINAL_FITS_FILE = "/tmp/N-FITS_WITH_EXTENSIONS.fits";
    protected static final String INITIAL_FITS_FILE = "FITS_WITH_EXTENSIONS.fits";

    @Before
    @Override
    public void removeFiles() {
        removeTestFile(FINAL_FITS_FILE);
        removeTestFile("/tmp/" + INITIAL_FITS_FILE);
    }

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

    //@Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException, FitsParseException {
        TimeUnit.MILLISECONDS.sleep(400);
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile(INITIAL_FITS_FILE, "/tmp/" + INITIAL_FITS_FILE);

        Set<String> originalKeywords = readKeywords("/tmp/" + INITIAL_FITS_FILE);
        System.out.println(originalKeywords);
        sendObservationEvents(eventHandler, new DataLabel("FITS_WITH_EXTENSIONS"));

        File finalFile = new File(FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Hedit hEdit = new Hedit(new File(FINAL_FITS_FILE));
        Header primaryHeader = hEdit.readPrimary();
        Set<String> afterProcessingPrimaryKeywords = primaryHeader.getKeywords();

        System.out.println(afterProcessingPrimaryKeywords);

        assertTrue(afterProcessingPrimaryKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingPrimaryKeywords.contains("WINDSPEE"));
    }

}

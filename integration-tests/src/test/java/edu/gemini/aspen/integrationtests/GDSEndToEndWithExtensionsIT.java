package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory;
import edu.gemini.aspen.gds.api.CompositeErrorPolicy;
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
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

        copyInitialFile(INITIAL_FITS_FILE, "/tmp/" + INITIAL_FITS_FILE);

        List<Set<String>> originalKeywords = readAllExtensionsKeywords("/tmp/" + INITIAL_FITS_FILE);
        System.out.println(originalKeywords);
        assertEquals(3, originalKeywords.size()); //primary + 2 extensions

        sendObservationEvents(eventHandler, new DataLabel("FITS_WITH_EXTENSIONS"));
        TimeUnit.MILLISECONDS.sleep(1000);

        File finalFile = new File(FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Hedit hEdit = new Hedit(finalFile);
        List<Header> allHeaders = hEdit.readAllHeaders();
        assertEquals(3, allHeaders.size()); //primary + 2 extensions

        List<Set<String>> afterProcessingAllExtensionsKeywords = new ArrayList<Set<String>>();
        for (Header header : allHeaders) {
            afterProcessingAllExtensionsKeywords.add(header.getKeywords());
        }

        System.out.println(afterProcessingAllExtensionsKeywords);

        for (int i = 0; i < originalKeywords.size(); i++) {
            assertTrue(afterProcessingAllExtensionsKeywords.get(i).containsAll(originalKeywords.get(i)));
        }

        assertTrue(afterProcessingAllExtensionsKeywords.get(0).contains("WINDSPEE"));
        assertTrue(afterProcessingAllExtensionsKeywords.get(0).contains("WINDDIRE"));

        assertTrue(afterProcessingAllExtensionsKeywords.get(1).contains("AIRMASS"));
        assertTrue(afterProcessingAllExtensionsKeywords.get(1).contains("HUMIDITY"));
        assertTrue(afterProcessingAllExtensionsKeywords.get(1).contains("TAMBIENT"));
        assertTrue(afterProcessingAllExtensionsKeywords.get(1).contains("PRESSURE"));

    }

}

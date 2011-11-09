package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.fits.FitsParseException;
import edu.gemini.fits.Header;
import edu.gemini.fits.Hedit;
import org.junit.Before;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

/**
 * Base class for the integration tests related to the GDS
 */
public class GDSIntegrationBase extends FelixContainerConfigurationBase {
    protected static final String FINAL_FITS_FILE = "S20110427-01.fits";
    protected static final String INITIAL_FITS_FILE = "S20110427-01.fits";
    protected static final String INITIAL_FITS_DIR = "/tmp/";
    protected static final String FINAL_FITS_DIR = "/tmp/perm/";

    @Before
    public void removeFiles() {
        removeTestFile(FINAL_FITS_DIR + FINAL_FITS_FILE);
        removeTestFile(INITIAL_FITS_DIR + INITIAL_FITS_FILE);
    }

    @Configuration
    public static Option[] gdsBundles() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("file-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("fits-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("scala-library").groupId("com.weiglewilczek.scala-lang-osgi").versionAsInProject(),
                mavenBundle().artifactId("gds-api").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.javax.xml.stream").groupId("javax.xml.stream").versionAsInProject(),
                mavenBundle().artifactId("com.springsource.org.dom4j").groupId("org.dom4j").versionAsInProject(),
                mavenBundle().artifactId("gmp-services").groupId("edu.gemini.aspen.gmp").versionAsInProject(),
                mavenBundle().artifactId("gds-keywords-database").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-performance-monitoring").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("time_2.9.1").groupId("edu.gemini.external.osgi.org.scala-tools.time").versionAsInProject(),
                mavenBundle().artifactId("joda-time").groupId("joda-time").versionAsInProject(),
                mavenBundle().artifactId("gds-actors-composer").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-fits-updater").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-observation-state-publisher").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-obsevent-handler").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("shared-test").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("shared-util").groupId("gemini-nocs").versionAsInProject()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/services";
    }

    protected void removeTestFile(String fileName) {
        File finalFile = new File(fileName);
        if (finalFile.exists()) {
            finalFile.delete();
        }
    }

    protected void copyInitialFile() throws IOException, URISyntaxException {
        copyInitialFile(INITIAL_FITS_FILE, INITIAL_FITS_DIR + INITIAL_FITS_FILE);
    }

    protected void copyInitialFile(String src, String dest) throws IOException, URISyntaxException {
        InputStream in = GDSWithODBIT.class.getResourceAsStream(src);
        assertTrue(in.available() > 0);

        FileOutputStream fos = new FileOutputStream(dest);
        byte readBlock[] = new byte[1024];
        while (in.available() > 0) {
            int readCount = in.read(readBlock);
            fos.write(readBlock, 0, readCount);
        }
        fos.close();
    }

    protected void sendObservationEvents(ObservationEventHandler eventHandler, DataLabel dataLabel) throws InterruptedException {
        eventHandler.onObservationEvent(ObservationEvent.OBS_PREP, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_START_READOUT, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_READOUT, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_START_DSET_WRITE, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, dataLabel);

        TimeUnit.MILLISECONDS.sleep(300);
    }

    protected Set<String> readFinalKeywords() throws IOException, FitsParseException, InterruptedException {
        return readKeywords(FINAL_FITS_DIR + FINAL_FITS_FILE);
    }

    protected Set<String> readKeywords(String fileName) throws IOException, FitsParseException, InterruptedException {
        Hedit hEdit = new Hedit(new File(fileName));
        Header primaryHeader = hEdit.readPrimary();
        return primaryHeader.getKeywords();
    }

    protected List<Set<String>> readAllExtensionsKeywords(String fileName) throws IOException, FitsParseException, InterruptedException {
        Hedit hEdit = new Hedit(new File(fileName));
        List<Header> allHeaders = hEdit.readAllHeaders();
        List<Set<String>> allKeywords = new ArrayList<Set<String>>();
        for (Header header : allHeaders) {
            allKeywords.add(header.getKeywords());
        }
        return allKeywords;
    }

    protected Header readFinalPrimary() throws IOException, FitsParseException, InterruptedException {
        return readPrimary(FINAL_FITS_DIR + FINAL_FITS_FILE);

    }

    protected Header readPrimary(String fileName) throws IOException, FitsParseException, InterruptedException {
        Hedit hEdit = new Hedit(new File(fileName));
        return hEdit.readPrimary();
    }

    protected Set<String> readOriginalKeywords() throws IOException, FitsParseException, InterruptedException {
        return readKeywords(INITIAL_FITS_DIR + INITIAL_FITS_FILE);
    }
}

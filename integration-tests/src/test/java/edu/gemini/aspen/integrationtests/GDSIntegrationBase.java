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
    protected static final String FINAL_FITS_FILE = "/tmp/N-S20110427-01.fits";
    protected static final String INITIAL_FITS_FILE = "/tmp/S20110427-01.fits";

    @Before
    public void removeFiles() {
        removeTestFile(FINAL_FITS_FILE);
        removeTestFile(INITIAL_FITS_FILE);
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
                mavenBundle().artifactId("gds-keywords-database").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-performance-monitoring").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("time_2.8.1").groupId("edu.gemini.aspen.giapi.external").versionAsInProject(),
                mavenBundle().artifactId("joda-time").groupId("joda-time").versionAsInProject(),
                mavenBundle().artifactId("gds-actors-composer").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-fits-updater").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-observation-state-publisher").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-obsevent-handler").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/services";
    }

    private void removeTestFile(String fileName) {
        File finalFile = new File(fileName);
        if (finalFile.exists()) {
            finalFile.delete();
        }
    }

    protected void copyInitialFile() throws IOException {
        InputStream in = GDSWithODBIT.class.getResourceAsStream("S20110427-01.fits");
        assertTrue(in.available() > 0);

        FileOutputStream fos = new FileOutputStream(INITIAL_FITS_FILE);
        byte readBlock[] = new byte[1024];
        while (in.available() > 0) {
            int readCount = in.read(readBlock);
            fos.write(readBlock, 0, readCount);
        }
        fos.close();
    }

    protected void sendObservationEvents(ObservationEventHandler eventHandler) throws InterruptedException {
        DataLabel dataLabel = new DataLabel("S20110427-01");
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
        return readKeywords(FINAL_FITS_FILE);
    }

    private Set<String> readKeywords(String fileName) throws IOException, FitsParseException, InterruptedException {
        Hedit hEdit = new Hedit(new File(fileName));
        Header primaryHeader = hEdit.readPrimary();
        //Header soc = hEdit.readAllHeaders().get(1);
        //System.out.println("EXT 1 " + soc.getKeywords());
        return primaryHeader.getKeywords();
    }

    protected Set<String> readOriginalKeywords() throws IOException, FitsParseException, InterruptedException {
        return readKeywords(INITIAL_FITS_FILE);
    }
}

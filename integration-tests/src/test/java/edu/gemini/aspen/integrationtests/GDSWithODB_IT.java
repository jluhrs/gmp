package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import edu.gemini.fits.FitsParseException;
import edu.gemini.fits.Header;
import edu.gemini.fits.Hedit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class GDSWithODB_IT extends FelixContainerConfigurationBase {
    @Configuration
    public static Option[] gdsBundles() {
        return options(
                vmOption("-Xverify:none "),
                systemProperty("jini.lus.import.hosts").value("sbfswgdev01.cl.gemini.edu"),
                systemProperty("org.osgi.framework.system.packages.extra").value("sun.misc,sun.security.action,sun.rmi.runtime,edu.gemini.rmi.server"),
                systemProperty("org.osgi.framework.bootdelegation").value("java.rmi.server"),
                systemProperty("jini.lus.import.groups").value("swg-test"),
                systemProperty("org.osgi.service.http.port").value("8888"),
                mavenBundle().artifactId("giapi").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("file-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("fits-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("scala-library").groupId("com.weiglewilczek.scala-lang-osgi").versionAsInProject(),
                mavenBundle().artifactId("gds-api").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-keywords-database").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-actors-composer").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-fits-updater").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-obsevent-handler").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("pax-web-jetty-bundle").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-spi").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").versionAsInProject(),
                mavenBundle().artifactId("jini-driver").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("spdb-activator").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("rr").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("gds-odb-actors").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    @Test
    public void bundleExistence() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(400);
        assertNotNull(getBundle("edu.gemini.aspen.gds.odb"));
    }

    //@Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException, FitsParseException {
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile();

        Hedit hEdit = new Hedit(new File("/tmp/S20110427-01.fits"));
        Header primaryHeader = hEdit.readPrimary();

        Set<String> originalKeywords = primaryHeader.getKeywords();


        TimeUnit.MILLISECONDS.sleep(100);
        DataLabel dataLabel = new DataLabel("S20110427-01");
        eventHandler.onObservationEvent(ObservationEvent.OBS_PREP, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, dataLabel);

        TimeUnit.MILLISECONDS.sleep(200);

        File finalFile = new File("/tmp/N-S20110427-01.fits");
        assertTrue(finalFile.exists());

        hEdit = new Hedit(finalFile);
        primaryHeader = hEdit.readPrimary();

        Set<String> afterProcessingKeywords = primaryHeader.getKeywords();
        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("AIRMASS"));
        assertTrue(afterProcessingKeywords.contains("HUMIDITY"));
        assertTrue(afterProcessingKeywords.contains("TAMBIENT"));
        assertTrue(afterProcessingKeywords.contains("PRESSURE"));
        assertTrue(afterProcessingKeywords.contains("WINDSPEE"));
        assertTrue(afterProcessingKeywords.contains("WINDDIRE"));
    }

    private void copyInitialFile() throws IOException {
        InputStream in = GDSWithODB_IT.class.getResourceAsStream("S20110427-01.fits");
        assertTrue(in.available() > 0);

        FileOutputStream fos = new FileOutputStream("/tmp/S20110427-01.fits");
        byte readBlock[] = new byte[1024];
        while (in.available() > 0) {
            int readCount = in.read(readBlock);
            fos.write(readBlock, 0, readCount);
        }
        fos.close();
    }

}

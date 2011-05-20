package edu.gemini.aspen.integrationtests;

import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class GDSEndToEndIT extends FelixContainerConfigurationBase {
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
                mavenBundle().artifactId("gds-actors-composer").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-fits-updater").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("epics-service").groupId("edu.gemini.epics").versionAsInProject(),
                mavenBundle().artifactId("jca-lib").groupId("edu.gemini.aspen").versionAsInProject(),
                mavenBundle().artifactId("gds-epics-actors").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-instrument-status-actors").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-obsevent-handler").groupId("edu.gemini.aspen.gds").versionAsInProject()
        );
    }

    //@Test
    public void bundleExistence() {
        assertNotNull(getBundle("edu.gemini.aspen.gds.api"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.keywords.database"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.actors"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.fits"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.epics"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.status"));
        assertNotNull(getBundle("edu.gemini.aspen.gds.obsevent.handler"));
    }

    @Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException {
        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        copyInitialFile();

        DataLabel dataLabel  = new DataLabel("S20110427-01");
        eventHandler.onObservationEvent(ObservationEvent.OBS_PREP, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_START_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_ACQ, dataLabel);

        TimeUnit.MILLISECONDS.sleep(100);
        eventHandler.onObservationEvent(ObservationEvent.OBS_END_DSET_WRITE, dataLabel);
    }

    private void copyInitialFile() throws IOException {
        InputStream in = GDSEndToEndIT.class.getResourceAsStream("S20110427-01.fits");
        System.out.println(GDSEndToEndIT.class.getResource("S20110427-01.fits"));
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

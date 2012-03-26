package edu.gemini.aspen.integrationtests;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.ByteStreams;
import edu.gemini.aspen.gds.api.fits.HeaderItem;
import edu.gemini.aspen.gds.fits.FitsReader;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEvent;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import org.junit.Before;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;
import static scala.collection.JavaConversions.seqAsJavaList;

/**
 * Base class for the integration tests related to the GDS
 */
public class GDSIntegrationBase extends FelixContainerConfigurationBase {
    protected static final String FINAL_FITS_FILE = "sample1.fits";
    protected static final String INITIAL_FITS_FILE = "sample1.fits";
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
                mavenBundle().artifactId("fits").groupId("edu.gemini.external.osgi.nom.tam").versionAsInProject(),
                mavenBundle().artifactId("scala-library").groupId("com.weiglewilczek.scala-lang-osgi").versionAsInProject(),
                mavenBundle().artifactId("gds-api").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("jms-api").groupId("edu.gemini.jms").versionAsInProject(),
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
                mavenBundle().artifactId("shared-util").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("pax-web-jetty-bundle").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-spi").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("ecj").groupId("edu.gemini.external.osgi.org.eclipse.jdt.core.compiler").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").versionAsInProject()
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
        ByteStreams.copy(GDSWithODBIT.class.getResourceAsStream(src), new FileOutputStream(dest));
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

    protected Set<String> readFinalKeywords() throws IOException, InterruptedException {
        return readKeywords(FINAL_FITS_DIR + FINAL_FITS_FILE, 0);
    }

    protected Set<String> readKeywords(String fileName, int header) throws IOException, InterruptedException {
        FitsReader reader = new FitsReader(new File(fileName));
        List<HeaderItem<?>> keyList = (List<HeaderItem<?>>)seqAsJavaList(reader.header(header).get().keywords());
        Set<String> set = Sets.newTreeSet();
        for (HeaderItem<?> h:keyList) {
            set.add(h.keywordName().key());
        }
        return set;
    }

    protected List<Set<String>> readAllExtensionsKeywords(String fileName, int headersCount) throws IOException, InterruptedException {
        List<Set<String>> extensions = Lists.newArrayList();
        for (int i=0;i<headersCount;i++) {
            extensions.add(readKeywords(fileName, i));
        }
        return extensions;
    }

    /*protected Header readFinalPrimary() throws IOException, InterruptedException {
        return readPrimary(FINAL_FITS_DIR + FINAL_FITS_FILE);

    }

    protected Header readPrimary(String fileName) throws IOException, InterruptedException {
        Hedit hEdit = new Hedit(new File(fileName));
        return hEdit.readPrimary();
    }*/

    protected Set<String> readOriginalKeywords() throws IOException, InterruptedException {
        return readKeywords(INITIAL_FITS_DIR + INITIAL_FITS_FILE, 0);
    }
}

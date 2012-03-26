package edu.gemini.aspen.integrationtests;

import com.google.common.collect.Sets;
import edu.gemini.aspen.gds.actors.factory.CompositeActorsFactory;
import edu.gemini.aspen.gds.api.CompositeErrorPolicy;
import edu.gemini.aspen.gds.api.ErrorPolicy;
import edu.gemini.aspen.gds.api.configuration.GDSConfigurationService;
import edu.gemini.aspen.gds.api.fits.HeaderItem;
import edu.gemini.aspen.gds.fits.FitsReader;
import edu.gemini.aspen.gds.keywords.database.KeywordsDatabase;
import edu.gemini.aspen.gds.keywords.database.ProgramIdDatabase;
import edu.gemini.aspen.gds.observationstate.ObservationStateProvider;
import edu.gemini.aspen.gds.observationstate.ObservationStatePublisher;
import edu.gemini.aspen.gds.observationstate.ObservationStateRegistrar;
import edu.gemini.aspen.giapi.data.DataLabel;
import edu.gemini.aspen.giapi.data.ObservationEventHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static scala.collection.JavaConversions.seqAsJavaList;

@RunWith(JUnit4TestRunner.class)
public class GDSWithODBAndEliminateErrorsPolicyIT extends GDSIntegrationBase {

    @Configuration
    public static Option[] gdsODBBundles() {
        return options(
                systemProperty("jini.lus.import.hosts").value("sbfswgdev01.cl.gemini.edu"),
                systemProperty("org.osgi.framework.system.packages.extra").value("sun.misc,sun.security.action,sun.rmi.runtime,edu.gemini.rmi.server"),
                systemProperty("org.osgi.framework.bootdelegation").value("java.rmi.server"),
                systemProperty("jini.lus.import.groups").value("swg-test"),
                systemProperty("org.osgi.service.http.port").value("8888"),
                mavenBundle().artifactId("pax-web-jetty-bundle").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-spi").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("org.osgi.compendium").groupId("org.osgi").versionAsInProject(),
                mavenBundle().artifactId("jini-driver").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("spdb-activator").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("rr").groupId("gemini-nocs").versionAsInProject(),
                mavenBundle().artifactId("gds-odb-actors").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                mavenBundle().artifactId("gds-error-policy").groupId("edu.gemini.aspen.gds").versionAsInProject(),
                waitForFrameworkStartup()
        );
    }

    @Override
    protected String confDir() {
        return "/src/test/resources/conf/gds_with_error_policy";
    }

    @Test
    public void bundleExistence() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(400);
        assertNotNull(getBundle("edu.gemini.aspen.gds.odb"));
    }

    @Test
    public void sendObsEvents() throws InterruptedException, URISyntaxException, IOException{
        TimeUnit.MILLISECONDS.sleep(600);
        assertNotNull(context.getService(context.getServiceReference(GDSConfigurationService.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositeActorsFactory.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(KeywordsDatabase.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(CompositeErrorPolicy.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStatePublisher.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateRegistrar.class.getName())));
        assertNotNull(context.getService(context.getServiceReference(ObservationStateProvider.class.getName())));

        TimeUnit.MILLISECONDS.sleep(400);

        //verify that the error policies are loaded
        Set<String> errorPolicies = Sets.newHashSet();
        try {
            for (ServiceReference ref : context.getServiceReferences(ErrorPolicy.class.getName(), null)) {
                errorPolicies.add(context.getService(ref).getClass().getName());
            }
        } catch (InvalidSyntaxException ex) {
            fail();
        }
        assertTrue(errorPolicies.contains("edu.gemini.aspen.gds.errorpolicy.ErrorsRemovedPolicy"));
        assertTrue(errorPolicies.contains("edu.gemini.aspen.gds.errorpolicy.EnforceMandatoryPolicy"));
        assertTrue(errorPolicies.contains("edu.gemini.aspen.gds.observationstate.impl.InspectPolicy"));

        ObservationEventHandler eventHandler = (ObservationEventHandler) context.getService(context.getServiceReference(ObservationEventHandler.class.getName()));
        assertNotNull(eventHandler);

        postProgramIDToDataLabelLink();

        copyInitialFile();

        Set<String> originalKeywords = readOriginalKeywords();

        assertFalse(originalKeywords.contains("PIFSTNAM"));

        sendObservationEvents(eventHandler, new DataLabel("sample1.fits"));
        TimeUnit.MILLISECONDS.sleep(2000);

        File finalFile = new File(FINAL_FITS_DIR + FINAL_FITS_FILE);
        assertTrue(finalFile.exists());

        Set<String> afterProcessingKeywords = readFinalKeywords();

        assertTrue(afterProcessingKeywords.containsAll(originalKeywords));
        assertTrue(afterProcessingKeywords.contains("PIFSTNAM"));
        assertTrue(afterProcessingKeywords.contains("EPIC"));
        assertTrue(afterProcessingKeywords.contains("EPIC2"));

        FitsReader reader = new FitsReader(finalFile);
        List<HeaderItem<?>> headerItems = (List<HeaderItem<?>>)seqAsJavaList(reader.header(0).get().keywords());
        for (HeaderItem<?> h:headerItems) {
            if (h.keywordName().equals("EPIC")) {
                assertEquals("default", h.value().toString());//non mandatory item should have default value if not found
            }
            if (h.keywordName().equals("EPIC2")) {
                assertEquals("", h.value().toString()); //mandatory item should be present but empty if not found
            }
        }
    }

    private void postProgramIDToDataLabelLink() {
        ProgramIdDatabase programIdDatabase = (ProgramIdDatabase) context.getService(context.getServiceReference(ProgramIdDatabase.class.getName()));
        assertNotNull(programIdDatabase);
        DataLabel dataLabel = new DataLabel("S20110427-01");
        programIdDatabase.store(dataLabel, "GS-2006B-Q-57");
    }

}

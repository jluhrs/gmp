package edu.gemini.aspen.integrationtests;

import edu.gemini.epics.JCAContextController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Basic integration test for EpicsService verifying that the services can run and its properties be set
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class EpicsServiceIT extends EpicsServiceBaseIntegration {

    @Test
    public void epicsServiceBundleStart() {
        assertNotNull(getEpicsServiceBundle());
        assertEquals(Bundle.ACTIVE, getEpicsServiceBundle().getState());
    }

    @Test
    public void jcaControllerHasStarted() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(300);
        ServiceReference reference = context.getServiceReference(JCAContextController.class.getName());
        assertNotNull(reference);
    }

}

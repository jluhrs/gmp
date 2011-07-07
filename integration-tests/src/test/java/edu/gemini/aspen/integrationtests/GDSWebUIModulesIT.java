package edu.gemini.aspen.integrationtests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

@RunWith(JUnit4TestRunner.class)
public class GDSWebUIModulesIT extends GDSIntegrationBase {
    @Configuration
    public static Option[] gdsEpicsBundles() {
        return options(
                vmOption("-Xverify:none "),
                mavenBundle().artifactId("gds-web-ui").groupId("edu.gemini.aspen.gds.ui").versionAsInProject(),
                mavenBundle().artifactId("vaadin").groupId("com.vaadin").versionAsInProject(),
                mavenBundle().artifactId("pax-web-jetty-bundle").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-extender-whiteboard").groupId("org.ops4j.pax.web").versionAsInProject(),
                mavenBundle().artifactId("pax-web-spi").groupId("org.ops4j.pax.web").versionAsInProject()
        );
    }

    @Test
    public void testAddRemoveModule() throws Exception {
        //WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://localhost:8888/";
        //Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);
        //selenium.start();

       // selenium.open("/gds");
        //assertEquals("GDS Management Console", selenium.getTitle());
    //    assertTrue(selenium.isTextPresent("Status"));

      //  selenium.stop();
    }

}

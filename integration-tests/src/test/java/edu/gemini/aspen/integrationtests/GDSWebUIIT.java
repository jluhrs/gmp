package edu.gemini.aspen.integrationtests;

import com.thoughtworks.selenium.Selenium;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.ops4j.pax.runner.*;
import org.ops4j.pax.runner.platform.DefaultJavaRunner;

import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * These are integration tests for the Web UI of the GDS that use selenium
 *
 * An instance of GDS is started using pax-runner and the test are carried on in a separate thread
 */
public class GDSWebUIIT extends GDSIntegrationBase {
    private static DefaultJavaRunner javaRunner;
    private static Run runner;
    private static int GDS_HTTP_PORT = 9999;

    @BeforeClass
    public static void launchContainer() throws URISyntaxException, InterruptedException {
        runner = new Run();

        String modulesURI = GDSIntegrationBase.class.getResource("gdswebui.properties").toURI().toString();

        // Change some properties to avoid port clashes
        String vmoProps = "--vmo=" +
                " -Dconf.base=../src/main/etc/conf" +
                " -Dlogs.dir=logs" +
                " -Xverify:none" +
                (" -Dorg.osgi.service.http.port=" + GDS_HTTP_PORT) +
                " -Dosgi.shell.telnet.port=16001" +
                " -Dorg.osgi.framework.system.packages.extra=sun.misc,sun.security.action,sun.rmi.runtime,edu.gemini.rmi.server";

        final CommandLine commandLine = new CommandLineImpl(vmoProps, "--clean", "scan-composite:" + modulesURI);

        final Configuration config = new ConfigurationImpl("classpath:META-INF/runner.properties");
        javaRunner = new DefaultJavaRunner();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runner.start(commandLine, config, new OptionResolverImpl(commandLine, config), javaRunner);
            }
        }).start();

        // Need to wait until the gmp-server is ready
        // todo: replace by a test on the http port
        TimeUnit.SECONDS.sleep(15);
    }

    @AfterClass
    public static void stop() {
        javaRunner.shutdown();
    }

    @Test
    public void testTestStatus() throws Exception {
        WebDriver driver = new FirefoxDriver();
        String baseUrl = "http://localhost:" + GDS_HTTP_PORT + "/";
        Selenium selenium = new WebDriverBackedSelenium(driver, baseUrl);

        selenium.open("/gds");
        assertEquals("GDS Management Console", selenium.getTitle());
        assertTrue(selenium.isTextPresent("Status"));

        selenium.stop();
    }

}

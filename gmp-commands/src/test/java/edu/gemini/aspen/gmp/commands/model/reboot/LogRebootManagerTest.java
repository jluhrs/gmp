package edu.gemini.aspen.gmp.commands.model.reboot;

import edu.gemini.aspen.giapi.commands.RebootArgument;
import org.junit.Test;

public class LogRebootManagerTest {

    @Test
    public void testReboot() {
        LogRebootManager rebootManager = new LogRebootManager();

        // There is nothing really to test except for not throwing exceptions
        rebootManager.reboot(RebootArgument.GMP);
        rebootManager.reboot(RebootArgument.NONE);
        rebootManager.reboot(RebootArgument.REBOOT);
    }
}

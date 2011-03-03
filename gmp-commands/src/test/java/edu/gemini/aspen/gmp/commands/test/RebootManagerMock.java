package edu.gemini.aspen.gmp.commands.test;

import edu.gemini.aspen.gmp.commands.model.RebootManager;
import edu.gemini.aspen.giapi.commands.RebootArgument;
import org.junit.Ignore;

/**
 * A dummy test reboot manager for testing purposes.
 *
 */
public class RebootManagerMock implements RebootManager {
    private RebootArgument _arg;

    public void reboot(RebootArgument arg) {
        _arg = arg;
        //notify that this handler was invoked. 
        synchronized (this) {
            notifyAll();
        }
    }

    public RebootArgument getReceivedArgument() {
        return _arg;
    }

    public void reset() {
       _arg = null; 
    }

}

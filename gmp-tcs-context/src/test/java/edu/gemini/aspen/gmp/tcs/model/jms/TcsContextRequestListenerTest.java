package edu.gemini.aspen.gmp.tcs.model.jms;

import edu.gemini.aspen.gmp.tcs.jms.JmsTcsContextDispatcher;
import edu.gemini.aspen.gmp.tcs.jms.TcsContextRequestListener;
import org.junit.Test;

public class TcsContextRequestListenerTest {
    @Test
    public void testConstructor() {
        JmsTcsContextDispatcher dispatcher = null;
        new TcsContextRequestListener(dispatcher);
    }
}

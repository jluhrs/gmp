package edu.gemini.aspen.gmp.epics.top;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Class EpicsTopTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
public class EpicsTopTest {

    @Test
    public void testTop(){
        EpicsTop top = new EpicsTopImpl("gpitest");
        assertEquals("gpitest:test",top.buildChannelName("test"));
    }
}

package edu.gemini.gmp.top;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Class TopTest
 *
 * @author Nicolas A. Barriga
 *         Date: 4/6/11
 */
public class TopTest {

    @Test
    public void testTop(){
        Top top = new TopImpl("gpitest","gpitest");
        assertEquals("gpitest:test", top.buildEpicsChannelName("test"));
        assertEquals("gpitest:test", top.buildStatusItemName("test"));
    }
}

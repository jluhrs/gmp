package edu.gemini.aspen.giapi.commands;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Class ConfigPathTest
 *
 * @author Nicolas A. Barriga
 *         Date: 1/19/11
 */
public class ConfigPathTest {
    @Test
    public void testBasic(){
        ConfigPath path=new ConfigPath("my:test:path");
        assertArrayEquals(new String[]{"my","test","path"},path.split());
        assertEquals("path",path.getReferencedName());
        assertTrue(path.equals(new ConfigPath("my:test:path")));
        assertEquals(new ConfigPath("my:test"),path.getParent());
        assertEquals(new ConfigPath("my:test"), path.getChildPath(new ConfigPath("my")));
    }
}

package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.EMPTY_PATH;
import static edu.gemini.aspen.giapi.commands.ConfigPath.configPath;
import static org.junit.Assert.*;

/**
 * Class ConfigPathTest
 *
 * @author Nicolas A. Barriga
 *         Date: 1/19/11
 */
public class ConfigPathTest {
    @Test
    public void testConstructionWithString() {
        ConfigPath testPath = configPath("my:test:configPath");
        assertArrayEquals(new String[]{"my", "test", "configPath"}, testPath.split());
        assertEquals("configPath", testPath.getReferencedName());
        assertEquals(configPath("my:test"), testPath.getParent());
        assertEquals("my:test:configPath", testPath.getName());
        assertTrue(testPath.startsWith(configPath("my")));
        assertTrue(testPath.startsWith(configPath("my:test")));
        assertTrue(testPath.equals(configPath("my:test:configPath")));
        assertEquals(configPath("my:test"), testPath.getChildPath(configPath("my")));
    }

    @Test
    public void testConstructionWithChild() {
        ConfigPath testPath = configPath("X", "val1");
        assertArrayEquals(new String[]{"X", "val1"}, testPath.split());
        assertEquals("val1", testPath.getReferencedName());
        assertEquals(configPath("X"), testPath.getParent());
        assertEquals("X:val1", testPath.getName());
        assertEquals("X:val1", testPath.toString());
        assertTrue(testPath.startsWith(configPath("X")));
        assertEquals(configPath("X:val1"), testPath.getChildPath(configPath("X")));
        assertEquals(EMPTY_PATH, testPath.getChildPath(configPath("val1")));
    }

    @Test
    public void testConstructionsWithSeparator() {
        ConfigPath path = configPath("X:", "val1");
        assertEquals("X:val1", path.getName());
        path = configPath("X", ":val1");
        assertEquals("X:val1", path.getName());
        path = configPath("X:", ":val1");
        assertEquals("X:val1", path.getName());
    }

    @Test
    public void testEquality() {
        ConfigPath a = configPath("X", "val1");
        ConfigPath b = configPath("X", "val1");
        ConfigPath c = configPath("X", "val2");

        new EqualsTester(a, b, c, null);
    }

    @Test
    public void testComparison() {
        ConfigPath a = configPath("X", "val1");
        ConfigPath b = configPath("X", "val1");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    public void testPathOnlyWithSeparator() {
        ConfigPath path = configPath(":");
        assertArrayEquals(new String[]{""}, path.split());
        assertEquals("", path.getName());
        assertEquals("", path.getReferencedName());
        assertEquals(EMPTY_PATH, path.getParent());
        assertTrue(path.getParent().startsWith(EMPTY_PATH));
        assertFalse(path.getParent().startsWith(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstruction1() {
        configPath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstruction2() {
        configPath("parent", null);
    }

}

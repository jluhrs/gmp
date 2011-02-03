package edu.gemini.aspen.giapi.commands;

import com.gargoylesoftware.base.testing.EqualsTester;
import org.junit.Test;

import static edu.gemini.aspen.giapi.commands.ConfigPath.EMPTY_PATH;
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
        ConfigPath path = new ConfigPath("my:test:path");
        assertArrayEquals(new String[]{"my", "test", "path"}, path.split());
        assertEquals("path", path.getReferencedName());
        assertEquals(new ConfigPath("my:test"), path.getParent());
        assertEquals("my:test:path", path.getName());
        assertTrue(path.startsWith(new ConfigPath("my")));
        assertTrue(path.startsWith(new ConfigPath("my:test")));
        assertTrue(path.equals(new ConfigPath("my:test:path")));
        assertEquals(new ConfigPath("my:test"), path.getChildPath(new ConfigPath("my")));
    }

    @Test
    public void testConstructionWithChild() {
        ConfigPath path = new ConfigPath("X", "val1");
        assertArrayEquals(new String[]{"X", "val1"}, path.split());
        assertEquals("val1", path.getReferencedName());
        assertEquals(new ConfigPath("X"), path.getParent());
        assertEquals("X:val1", path.getName());
        assertEquals("X:val1", path.toString());
        assertTrue(path.startsWith(new ConfigPath("X")));
        assertEquals(new ConfigPath("X:val1"), path.getChildPath(new ConfigPath("X")));
        assertEquals(EMPTY_PATH, path.getChildPath(new ConfigPath("val1")));
    }

    @Test
    public void testConstructionsWithSeparator() {
        ConfigPath path = new ConfigPath("X:", "val1");
        assertEquals("X:val1", path.getName());
        path = new ConfigPath("X", ":val1");
        assertEquals("X:val1", path.getName());
        path = new ConfigPath("X:", ":val1");
        assertEquals("X:val1", path.getName());
    }

    @Test
    public void testEquality() {
        ConfigPath a = new ConfigPath("X", "val1");
        ConfigPath b = new ConfigPath("X", "val1");
        ConfigPath c = new ConfigPath("X", "val2");
        ConfigPath d = new ConfigPath("X", "val1") {
        };

        new EqualsTester(a, b, c, d);
    }

    @Test
    public void testComparison() {
        ConfigPath a = new ConfigPath("X", "val1");
        ConfigPath b = new ConfigPath("X", "val1");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    public void testPathOnlyWithSeparator() {
        ConfigPath path = new ConfigPath(":");
        assertArrayEquals(new String[]{""}, path.split());
        assertEquals("", path.getName());
        assertEquals("", path.getReferencedName());
        assertEquals(EMPTY_PATH, path.getParent());
        assertTrue(path.getParent().startsWith(EMPTY_PATH));
        assertFalse(path.getParent().startsWith(null));
        System.out.println(path.getReferencedName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstruction1() {
        new ConfigPath(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConstruction2() {
        new ConfigPath("parent", null);
    }

}

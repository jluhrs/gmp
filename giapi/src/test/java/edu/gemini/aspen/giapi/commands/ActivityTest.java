package edu.gemini.aspen.giapi.commands;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ActivityTest {
    @Test
    public void testGetName() {
        assertEquals("PRESET/START", Activity.PRESET_START.getName());
    }

    @Test
    public void testToActivity() {
        assertEquals(Activity.PRESET_START, Activity.toActivity("PRESET/START"));
        assertEquals(Activity.CANCEL, Activity.toActivity("CANCEL"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToActivityWithNull() {
        Activity.toActivity(null);
    }
}

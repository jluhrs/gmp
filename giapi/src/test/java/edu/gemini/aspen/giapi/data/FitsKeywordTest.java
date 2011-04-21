package edu.gemini.aspen.giapi.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FitsKeywordTest {
    @Test
    public void testExactKeyword() {
        FitsKeyword keyword = new FitsKeyword("KEYWORD1");

        assertEquals("KEYWORD1", keyword.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKeyword() {
        new FitsKeyword(null);
    }

    @Test
    public void testLowerCaseKeyword() {
        FitsKeyword keyword = new FitsKeyword("Keyword1");

        assertEquals("KEYWORD1", keyword.getName());
    }
}

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

    @Test
    public void testValidFormats() {
        // 8 chars long
        FitsKeyword keyword = new FitsKeyword("KEYWORDK");

        assertEquals("KEYWORDK", keyword.getName());

        // 1 long pure chars
        keyword = new FitsKeyword("K");

        assertEquals("K", keyword.getName());

        // Numbers
        keyword = new FitsKeyword("12345678");

        assertEquals("12345678", keyword.getName());

        // underscore and hyphen
        keyword = new FitsKeyword("ABC_DE-4");

        assertEquals("ABC_DE-4", keyword.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testKeywordTooLong() {
        // 9 chars long
        new FitsKeyword("KEYWORDKE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyKeyword() {
        // 9 chars long
        new FitsKeyword("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotValidChar() {
        // 9 chars long
        new FitsKeyword("@KEY");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSpaces() {
        // 9 chars long
        new FitsKeyword("KEY WORD");
    }
}

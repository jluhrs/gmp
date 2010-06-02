package edu.gemini.aspen.giapi.data;

import org.junit.Test;
import static org.junit.Assert.*;


public class FileEventTest {



    @Test
    public void testGetFileEventByCode() {

        assertEquals(FileEvent.ANCILLARY_FILE, FileEvent.getByCode(0));
        assertEquals(FileEvent.INTERMEDIATE_FILE, FileEvent.getByCode(1));

    }

    @Test
    public void testGetInvalidFileEventByCode() {

        assertNull(FileEvent.getByCode(-1));
        assertNull(FileEvent.getByCode(2));

    }


}

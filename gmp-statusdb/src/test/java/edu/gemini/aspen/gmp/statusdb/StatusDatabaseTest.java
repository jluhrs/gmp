package edu.gemini.aspen.gmp.statusdb;

import edu.gemini.aspen.giapi.status.impl.BasicStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * StatusDatabase Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>01/17/2011</pre>
 */
public class StatusDatabaseTest {
    private static final String STATUS_NAME = "status";
    private StatusDatabase statusDatabase;
    private BasicStatus<Integer> item;

    @Before
    public void setUp() throws Exception {
        statusDatabase = new StatusDatabase();
        item = new BasicStatus<Integer>(STATUS_NAME, Integer.valueOf(0));
    }

    /**
     * Test Method: getName()
     */
    @Test
    public void testGetName() throws Exception {
        assertNotNull(statusDatabase.getName());
    }

    /**
     * Test Method: getStatusItem(String name)
     */
    @Test
    public void testGetStatusItem() throws Exception {
        assertNull(statusDatabase.getStatusItem(STATUS_NAME));

        statusDatabase.update(item);
        assertEquals(item, statusDatabase.getStatusItem(STATUS_NAME));
    }

    /**
     * Test Method: getStatusNames()
     */
    @Test
    public void testGetStatusNames() throws Exception {
        assertTrue(statusDatabase.getStatusNames().isEmpty());

        statusDatabase.update(item);
        assertEquals(1, statusDatabase.getStatusNames().size());
        assertTrue(statusDatabase.getStatusNames().contains(STATUS_NAME));
    }

    /**
     * Test Method: getAll()
     */
    @Test
    public void testGetAll() throws Exception {
        assertFalse(statusDatabase.getAll().iterator().hasNext());

        statusDatabase.update(item);

        Iterator it = statusDatabase.getAll().iterator();
        assertTrue(it.hasNext());
        assertEquals(item, it.next());
        assertFalse(it.hasNext());
    }

    /**
     * Test the toString method
     */
    @Test
    public void testToString() throws Exception {
        assertEquals(statusDatabase.getName(), statusDatabase.toString());
    }


}

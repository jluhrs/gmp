package edu.gemini.aspen.gmp.services.jms;

import org.junit.Test;

import javax.jms.MapMessage;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JmsServiceRequestTest {
    @Test
    public void testConstruction() {
        MapMessage message = mock(MapMessage.class);
        assertEquals(message, new JmsServiceRequest(message).getMessage());
    }
}

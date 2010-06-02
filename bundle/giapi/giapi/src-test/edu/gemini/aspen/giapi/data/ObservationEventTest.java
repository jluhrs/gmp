package edu.gemini.aspen.giapi.data;

import org.junit.Test;
import static org.junit.Assert.*;
import com.gargoylesoftware.base.testing.EqualsTester;

/**
 * Unit tests for the Observation Event class.
 */
public class ObservationEventTest {

    @Test
    public void testEquals() {
        new EqualsTester(ObservationEvent.OBS_START_ACQ,
                ObservationEvent.OBS_START_ACQ,
                ObservationEvent.OBS_END_ACQ,
                null);
    }

    @Test
    public void testGetObservationEventName() {
        assertEquals(ObservationEvent.OBS_END_ACQ.getObservationEventName(), "OBS_END_ACQ");
        assertEquals(ObservationEvent.OBS_END_DSET_WRITE.getObservationEventName(), "OBS_END_DSET_WRITE");
        assertEquals(ObservationEvent.OBS_END_READOUT.getObservationEventName(), "OBS_END_READOUT");
        assertEquals(ObservationEvent.OBS_PREP.getObservationEventName(), "OBS_PREP");
        assertEquals(ObservationEvent.OBS_START_ACQ.getObservationEventName(), "OBS_START_ACQ");
        assertEquals(ObservationEvent.OBS_START_DSET_WRITE.getObservationEventName(), "OBS_START_DSET_WRITE");
        assertEquals(ObservationEvent.OBS_START_READOUT.getObservationEventName(), "OBS_START_READOUT");
    }

    @Test
    public void testValidParsing() {
        assertEquals(ObservationEvent.getObservationEvent("OBS_END_ACQ"), ObservationEvent.OBS_END_ACQ);
        assertEquals(ObservationEvent.getObservationEvent("OBS_END_DSET_WRITE"), ObservationEvent.OBS_END_DSET_WRITE);
        assertEquals(ObservationEvent.getObservationEvent("OBS_END_READOUT"), ObservationEvent.OBS_END_READOUT);
        assertEquals(ObservationEvent.getObservationEvent("OBS_PREP"), ObservationEvent.OBS_PREP);
        assertEquals(ObservationEvent.getObservationEvent("OBS_START_ACQ"), ObservationEvent.OBS_START_ACQ);
        assertEquals(ObservationEvent.getObservationEvent("OBS_START_DSET_WRITE"), ObservationEvent.OBS_START_DSET_WRITE);
        assertEquals(ObservationEvent.getObservationEvent("OBS_START_READOUT"), ObservationEvent.OBS_START_READOUT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidParsing() {
        ObservationEvent.getObservationEvent("unexistant event");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParsing() {
        ObservationEvent.getObservationEvent(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyParsing() {
        ObservationEvent.getObservationEvent("");
    }




}

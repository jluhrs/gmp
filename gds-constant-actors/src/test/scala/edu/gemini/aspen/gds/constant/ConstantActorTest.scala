package edu.gemini.aspen.gds.constant

import edu.gemini.aspen.gds.api.Conversions._
import org.junit.Test
import org.junit.Assert._
import edu.gemini.aspen.gds.api.{CollectedValue, GDSConfiguration}

class ConstantActorTest {

    @Test
    def testActor() {
        val constActor = new ConstantActor(buildConfiguration("key1", "val1") :: buildConfiguration("key2", "val2") :: Nil)
        assertEquals(CollectedValue("key1", "val1", "", 0) :: CollectedValue("key2", "val2", "", 0) :: Nil, constActor.collectValues())
    }

    private def buildConfiguration(keyword: String, value: String): GDSConfiguration = {
        GDSConfiguration("GPI",
            "OBS_START_ACQ",
            keyword,
            0,
            "STRING",
            false,
            value,
            "CONSTANT",
            "",
            0,
            "")
    }
}
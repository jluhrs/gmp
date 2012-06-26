package edu.gemini.aspen.gds.odb

import org.junit.Assert._
import org.junit.Test
import org.specs2.mock.Mockito
import edu.gemini.aspen.gds.api._
import edu.gemini.aspen.gds.api.Conversions._
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.keywords.database.impl.ProgramIdDatabaseImpl
import edu.gemini.aspen.gds.keywords.database.StoreProgramId
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

class ODBActorsFactoryTest extends Mockito {
    val databaseService = mock[IDBDatabaseService]
    val programIDDatabase = new ProgramIdDatabaseImpl()
    val programID = "GS-2011-Q-56"
    val dataLabel = new DataLabel("GS-2011")
    val odbActorsFactory = new ODBActorsFactory(databaseService, programIDDatabase)

    programIDDatabase ! StoreProgramId(dataLabel, programID)

    @Test
    def testConfigureWithOneItem {
        val configuration = buildOneConfiguration("OBS_PREP", "ODB1", "odb:piFirstName")
        odbActorsFactory.configure(configuration)

        val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
        assertEquals(1, actors.length)
    }

    @Test
    def testSkipNotRelatedEvent {
        val configuration = buildOneConfiguration("OBS_START_ACQ", "ADB1", "odb:piFirstName")
        odbActorsFactory.configure(configuration)

        val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
        assertTrue(actors.isEmpty)
    }

    @Test
    def testConfigureWithTwoItems {
        val configuration = buildOneConfiguration("OBS_PREP", "STATUS1", "gpi:status1") :::
                buildOneConfiguration("OBS_PREP", "STATUS2", "gpi:status2")
        odbActorsFactory.configure(configuration)

        val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
        assertEquals(1, actors.length)
    }

    @Test
    def testSkipUnknownLabels {
        val dataLabel = new DataLabel("label")
        val actors = odbActorsFactory.buildActors(ObservationEvent.OBS_PREP, dataLabel)
        assertTrue(actors.isEmpty)
    }

    def buildOneConfiguration(event: String, keyword: String, channel: String) =
        List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "ODB", channel, 0, "", "A comment"))

    def buildOneNonODBConfiguration(event: String, keyword: String, channel: String) =
        List(GDSConfiguration("GPI", event, keyword, 0, "STRING", false, "NONE", "STATUS", channel, 0, "", "A comment"))

}
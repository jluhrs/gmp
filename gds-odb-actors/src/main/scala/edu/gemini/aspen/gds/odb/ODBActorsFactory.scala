package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import java.util.logging.Logger
import edu.gemini.pot.spdb.IDBDatabaseService

/**
 * Factory of Actors that can retrieve values from the ODB
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class ODBActorsFactory(@Requires dbService: IDBDatabaseService) extends KeywordActorsFactory {
    val LOG =  Logger.getLogger(classOf[ODBActorsFactory].getName)
    var actorsConfiguration: List[GDSConfiguration] = List()

    override def startAcquisitionActors(dataLabel: DataLabel) = {
        configurationsForEvent(ObservationEvent.OBS_START_ACQ) map {
            case config:GDSConfiguration => new ODBValueActor(dbService, config)
        }
    }

    override def endAcquisitionActors(dataLabel: DataLabel) = {
        configurationsForEvent(ObservationEvent.OBS_END_ACQ) map {
            case config:GDSConfiguration => new ODBValueActor(dbService, config)
        }
    }

    override def configure(configuration:List[GDSConfiguration]) {
        actorsConfiguration = configuration filter { _.subsystem.name == "ODB"}
    }

    def configurationsForEvent(e: ObservationEvent) = {
        actorsConfiguration filter {_.event.name == e.toString}
    }

    @Validate
    def start() {
        LOG.info("ODBActorsFactory started")
    }
}
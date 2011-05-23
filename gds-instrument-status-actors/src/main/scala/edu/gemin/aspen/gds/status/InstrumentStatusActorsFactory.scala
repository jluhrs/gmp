package edu.gemini.aspen.gds.status

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import edu.gemin.aspen.gds.status.InstrumentStatusActor
import edu.gemini.aspen.giapi.status.StatusDatabaseService
import java.util.logging.Logger

/**
 * Factory of Actors that can retrieve instrument status
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class InstrumentStatusActorsFactory(@Requires statusDB: StatusDatabaseService) extends KeywordActorsFactory {
    val LOG =  Logger.getLogger(classOf[InstrumentStatusActorsFactory].getName)
    var actorsConfiguration: List[GDSConfiguration] = List()

    override def buildStartAcquisitionActors(dataLabel: DataLabel) = {
        configurationsForEvent(ObservationEvent.OBS_START_ACQ) map {
            new InstrumentStatusActor(statusDB, _)
        }
    }

    override def buildEndAcquisitionActors(dataLabel: DataLabel) = {
        configurationsForEvent(ObservationEvent.OBS_END_ACQ) map {
            new InstrumentStatusActor(statusDB, _)
        }
    }

    override def configure(configuration:List[GDSConfiguration]) {
        actorsConfiguration = configuration filter { _.subsystem.name == "STATUS"}
    }

    /**
     * Filters out only the configuration events relevant for a given observation event
     */
    private def configurationsForEvent(e: ObservationEvent) = {
        actorsConfiguration filter {_.event.name == e.toString}
    }

    @Validate
    def start() {
        LOG.info("InstrumentStatusActorFactory started")
    }
}
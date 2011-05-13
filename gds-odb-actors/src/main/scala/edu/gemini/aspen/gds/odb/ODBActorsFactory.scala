package edu.gemini.aspen.gds.odb

import edu.gemini.aspen.gds.api.KeywordActorsFactory
import edu.gemini.aspen.gds.api.GDSConfiguration
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}
import java.util.logging.Logger
import edu.gemini.pot.spdb.IDBDatabaseService

/**
 * Factory of Actors that can retrieve values from the ODB
 *
 * Whereas other actor factories use the approach of one actor per keyword, the ODB
 * used one actor for all the keywords. This is to reduce the cost of ODB calls which
 * tend to be expensive
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordActorsFactory]))
class ODBActorsFactory(@Requires dbService: IDBDatabaseService) extends KeywordActorsFactory {
    val LOG =  Logger.getLogger(classOf[ODBActorsFactory].getName)
    var actorsConfiguration: List[GDSConfiguration] = List()

    override def buildInitializationActors(programID:String, dataLabel:DataLabel) = {
        new ODBValuesActor(dbService.getQueryRunner, actorsConfiguration) :: Nil
    }

    override def buildStartAcquisitionActors(dataLabel: DataLabel) = {
        List()
    }

    override def buildEndAcquisitionActors(dataLabel: DataLabel) = {
        List()
    }

    override def configure(configuration:List[GDSConfiguration]) {
        actorsConfiguration = configuration filter { _.subsystem.name == "ODB"}
    }

    @Validate
    def start() {
        LOG.info("ODBActorsFactory started")
    }
}
package edu.gemini.aspen.gds.odb

import org.apache.felix.ipojo.annotations._
import java.util.logging.Logger
import edu.gemini.pot.spdb.IDBDatabaseService
import edu.gemini.aspen.gds.api.{KeywordValueActor, KeywordActorsFactory, GDSConfiguration}
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, ProgramIdDatabase}
import edu.gemini.aspen.giapi.data.{ObservationEvent, DataLabel}

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
class ODBActorsFactory(@Requires dbService: IDBDatabaseService, @Requires programIdDatabase: ProgramIdDatabase) extends KeywordActorsFactory {
  val LOG = Logger.getLogger(classOf[ODBActorsFactory].getName)
  var actorsConfiguration: List[GDSConfiguration] = List()

  override def buildActors(obsEvent: ObservationEvent, dataLabel: DataLabel): List[KeywordValueActor] = {
    val programID = (programIdDatabase !? RetrieveProgramId(dataLabel)).asInstanceOf[Option[String]]
    // Only produce actors if the programID has been already stored in the programIdDatabase
    programID filter {
      _ => actorsConfiguration.nonEmpty
    } map {
      id => new ODBValuesActor(id, dbService, actorsConfiguration)
    } toList
  }

  override def configure(configuration: List[GDSConfiguration]) {
    actorsConfiguration = configuration filter {
      _.subsystem.name == "ODB"
    }
  }

  @Validate
  def start() {
    LOG.info("ODBActorsFactory started")
  }
}
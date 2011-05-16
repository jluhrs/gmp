package edu.gemini.aspen.gds.keywords.database

import actors.Actor
import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import edu.gemini.aspen.giapi.data.DataLabel

trait ProgramIdDatabase extends Actor

case class StoreProgramId(dataLabel: DataLabel, programId: String)

case class RetrieveProgramId(dataLabel: DataLabel)

@Component
@Instantiate
@Provides(specifications = Array(classOf[ProgramIdDatabase]))
class ProgramIdDatabaseImpl extends ProgramIdDatabase {

  start()

  def act() {
    loop {
      react {
        case StoreProgramId(dataLabel, programId) => store(dataLabel, programId)
        case RetrieveProgramId(dataLabel) => sender ! retrieve(dataLabel)
        case x: Any => throw new RuntimeException("Argument not known: " + x)
      }
    }
  }

  //todo: clean db
  private val map = collection.mutable.Map.empty[DataLabel, String]

  private def store(dataLabel: DataLabel, programId: String) {
    map += dataLabel -> programId
  }

  private def retrieve(dataLabel: DataLabel): Option[String] = {
    map.get(dataLabel)
  }

}


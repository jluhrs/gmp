package edu.gemini.aspen.gds.keywords.database.impl

import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, StoreProgramId, ProgramIdDatabase}

/**
  * Implementation of ProgramIdDatabase */
@Component
@Instantiate
@Provides(specifications = Array(classOf[ProgramIdDatabase]))
class ProgramIdDatabaseImpl extends ProgramIdDatabase {

  start()

  def act() {
    loop {
      react {
        case StoreProgramId(dataLabel, programId) => internalStore(dataLabel, programId)
        case RetrieveProgramId(dataLabel) => sender ! retrieve(dataLabel)
        case x => error("Argument not known: " + x)
      }
    }
  }

  //todo: clean db
  private val map = collection.mutable.Map.empty[DataLabel, String]

  override def store(dataLabel: DataLabel, programId: String) {
    this ! StoreProgramId(dataLabel, programId)
  }

  private def internalStore(dataLabel: DataLabel, programId: String) {
    map += dataLabel -> programId
  }

  private def retrieve(dataLabel: DataLabel): Option[String] = {
    map.get(dataLabel)
  }

}
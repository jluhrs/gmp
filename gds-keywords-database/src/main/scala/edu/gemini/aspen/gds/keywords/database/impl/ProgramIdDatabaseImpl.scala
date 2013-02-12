package edu.gemini.aspen.gds.keywords.database.impl

import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.keywords.database.{RetrieveProgramId, StoreProgramId, ProgramIdDatabase}
import scala.collection.JavaConversions._
import java.util.concurrent.TimeUnit.MILLISECONDS
import collection.concurrent
import com.google.common.cache.CacheBuilder

/**
 * Implementation of ProgramIdDatabase */
@Component
@Instantiate
@Provides(specifications = Array[Class[_]](classOf[ProgramIdDatabase]))
class ProgramIdDatabaseImpl extends ProgramIdDatabase {
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  start()

  def act() {
    loop {
      react {
        case StoreProgramId(dataLabel, programId) => internalStore(dataLabel, programId)
        case RetrieveProgramId(dataLabel) => sender ! retrieve(dataLabel)
        case x => sys.error("Argument not known: " + x)
      }
    }
  }

  private val map: concurrent.Map[DataLabel, String] = CacheBuilder.newBuilder()
    .expireAfterWrite(expirationMillis, MILLISECONDS)
    .build[DataLabel, String]().asMap()

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
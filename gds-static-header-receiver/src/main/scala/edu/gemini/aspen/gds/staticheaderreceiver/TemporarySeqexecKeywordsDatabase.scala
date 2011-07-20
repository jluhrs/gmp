package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}
import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import actors.Actor
import edu.gemini.aspen.gds.staticheaderreceiver.TemporarySeqexecKeywordsDatabaseImpl.{Retrieve, Store, CleanAll, Clean}


/**
 * Companion object used to logically group message classes.
 */
object TemporarySeqexecKeywordsDatabaseImpl {

  // store a keyword/value pair for a given data label
  case class Store(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef)

  // retrieve the value associated to a keyword and data label
  case class Retrieve(dataLabel: DataLabel, keyword: FitsKeyword)

  // delete data for a given data label
  case class Clean(dataLabel: DataLabel)

  // delete the whole database
  case class CleanAll()

}

/**
 * Needed for iPojo
 */
trait TemporarySeqexecKeywordsDatabase extends Actor

/**
 * This component stores keyword values coming from the seqexec, so that later an actor will pick them up and
 * complete the information with data from the config file.
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[TemporarySeqexecKeywordsDatabase]))
class TemporarySeqexecKeywordsDatabaseImpl extends TemporarySeqexecKeywordsDatabase {
  start()

  def act() {
    loop {
      react {
        case Store(dataLabel, key, value) => store(dataLabel, key, value)
        case Retrieve(dataLabel, key) => reply(retrieveValue(dataLabel, key))
        case Clean(dataLabel) => clean(dataLabel)
        case CleanAll() => cleanAll()
        case x: Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }

  private val map = collection.mutable.Map.empty[DataLabel, collection.mutable.Map[FitsKeyword, AnyRef]]

  private def cleanAll() {
    map.clear()
  }

  private def clean(dataLabel: DataLabel) {
    map -= dataLabel
  }

  //todo: clean map. Empty maps are being left over for each datalabel.
  private def retrieveValue(dataLabel: DataLabel, keyword: FitsKeyword): Option[AnyRef] = {
    map.get(dataLabel) flatMap {
      x => x.get(keyword)
    }
  }

  private def store(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
    if (!map.contains(dataLabel)) {
      map += (dataLabel -> collection.mutable.Map.empty[FitsKeyword, AnyRef])
    }
    map(dataLabel) += (keyword -> value)
  }
}

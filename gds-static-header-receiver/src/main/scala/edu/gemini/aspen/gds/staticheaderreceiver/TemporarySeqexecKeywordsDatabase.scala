package edu.gemini.aspen.gds.staticheaderreceiver

import edu.gemini.aspen.giapi.data.{DataLabel, FitsKeyword}
import org.apache.felix.ipojo.annotations.{Component, Instantiate, Provides}
import edu.gemini.aspen.gds.api.TypedActor
import actors.{Channel, Actor}

/**
 * Companion object used to logically group message classes.
 */
sealed abstract class SeqexecDbMsg

case class Store(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) extends SeqexecDbMsg

case class Retrieve(dataLabel: DataLabel, keyword: FitsKeyword) extends SeqexecDbMsg

case class Clean(dataLabel: DataLabel) extends SeqexecDbMsg

case class CleanAll() extends SeqexecDbMsg


/**
 * Needed for iPojo
 */
trait TemporarySeqexecKeywordsDatabase extends TypedActor[SeqexecDbMsg]

/**
 * This component stores keyword values coming from the seqexec, so that later an actor will pick them up and
 * complete the information with data from the config file.
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[TemporarySeqexecKeywordsDatabase]))
class TemporarySeqexecKeywordsDatabaseImpl extends TemporarySeqexecKeywordsDatabase {

  val channel: actors.Channel[SeqexecDbMsg] = new Channel[SeqexecDbMsg](new Actor {
    start()

    def act() {
      loop {
        channel.react {
          case Store(dataLabel, key, value) => store(dataLabel, key, value)
          case Retrieve(dataLabel, key) => sender ! retrieveValue(dataLabel, key)
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
      if (map.contains(dataLabel)) {
        map(dataLabel).remove(keyword)
      } else {
        None
      }
    }

    private def store(dataLabel: DataLabel, keyword: FitsKeyword, value: AnyRef) {
      if (!map.contains(dataLabel)) {
        map += (dataLabel -> collection.mutable.Map.empty[FitsKeyword, AnyRef])
      }
      map(dataLabel) += (keyword -> value)
    }
  })
}

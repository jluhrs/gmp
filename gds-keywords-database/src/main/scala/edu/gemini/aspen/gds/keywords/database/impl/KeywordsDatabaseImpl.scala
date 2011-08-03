package edu.gemini.aspen.gds.keywords.database.impl

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectedValue
import java.util.logging.Logger
import scala._
import edu.gemini.aspen.gds.keywords.database._

/**
  * Component to store CollectedValue as HeaderItem, associated to DataLabel */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {
  private val LOG = Logger.getLogger(this.getClass.getName)

  start()

  def act() {
    loop {
      react {
        case Store(dataLabel: DataLabel, value: CollectedValue[_]) => store(dataLabel, List[CollectedValue[_]](value))
        case StoreList(dataLabel: DataLabel, value: List[CollectedValue[_]]) => store(dataLabel, value)
        case Retrieve(dataLabel) => reply(retrieve(dataLabel))
        case Clean(dataLabel) => clean(dataLabel)
        case x => error("Argument not known " + x)
      }
    }
  }

  private val map = collection.mutable.Map.empty[DataLabel, List[CollectedValue[_]]]

  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword*/
  private def store(dataLabel: DataLabel, headerItem: List[CollectedValue[_]]) {
    map.put(dataLabel, headerItem ++ map.getOrElse(dataLabel, List[CollectedValue[_]]()))
  }

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a List[CollectedValue[_]] containing the collected values of the data set
   * or an empty list if none found */
  private def retrieve(dataLabel: DataLabel): List[CollectedValue[_]] = map.getOrElse(dataLabel, Nil)

  /**
   * Remove all keywords associated with a given DataLabel
   *
   * @param dataLabel for which to remove data */
  private def clean(dataLabel: DataLabel) {
    map.remove(dataLabel)
  }

  @Validate
  def validate() {
    LOG.info("Validating KeywordsDatabase")
  }

  @Invalidate
  def invalidate() {
    LOG.info("Invalidating KeywordsDatabase")
  }
}
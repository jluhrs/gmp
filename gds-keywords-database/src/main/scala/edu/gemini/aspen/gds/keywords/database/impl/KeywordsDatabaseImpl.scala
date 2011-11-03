package edu.gemini.aspen.gds.keywords.database.impl

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import edu.gemini.aspen.gds.api.CollectedValue
import java.util.logging.Logger
import edu.gemini.aspen.gds.keywords.database._
import com.google.common.collect.MapMaker
import java.util.concurrent.TimeUnit._
import collection.mutable.ConcurrentMap
import scala.collection.JavaConversions._
import scala.actors.Actor._

/**
 * Component to store CollectedValue as HeaderItem, associated to DataLabel */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {
  private val LOG = Logger.getLogger(this.getClass.getName)
  // expiration of 1 day by default but tests can override it
  def expirationMillis = 24 * 60 * 60 * 1000

  start()

  def act() {
    loop {
      react {
        case Store(dataLabel: DataLabel, value: CollectedValue[_]) => _store(dataLabel, List[CollectedValue[_]](value))
        case StoreList(dataLabel: DataLabel, value: List[CollectedValue[_]]) => _store(dataLabel, value)
        case Retrieve(dataLabel) => reply(_retrieve(dataLabel))
        case Clean(dataLabel) => _clean(dataLabel)
        case x => sys.error("Argument not known " + x)
      }
    }
  }

  def store(dataLabel: DataLabel, value: CollectedValue[_]) {
    this ! Store(dataLabel, value)
  }

  def storeList(dataLabel: DataLabel, value: List[CollectedValue[_]]) {
    this ! StoreList(dataLabel, value)
  }

  def retrieve(dataLabel: DataLabel): List[CollectedValue[_]] = {
    val ret = this !?(1000, Retrieve(dataLabel))
    ret.asInstanceOf[Option[List[CollectedValue[_]]]] getOrElse Nil
  }

  def clean(dataLabel: DataLabel) {
    this ! Clean(dataLabel)
  }

  private val map: ConcurrentMap[DataLabel, List[CollectedValue[_]]] = new MapMaker().
    expireAfterWrite(expirationMillis, MILLISECONDS)
    .makeMap[DataLabel, List[CollectedValue[_]]]()

  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword*/
  private def _store(dataLabel: DataLabel, headerItem: List[CollectedValue[_]]) {
    map.put(dataLabel, headerItem ++ map.getOrElse(dataLabel, List[CollectedValue[_]]()))
  }

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a List[CollectedValue[_]] containing the collected values of the data set
   * or an empty list if none found */
  private def _retrieve(dataLabel: DataLabel): List[CollectedValue[_]] = map.getOrElse(dataLabel, Nil)

  /**
   * Remove all keywords associated with a given DataLabel
   *
   * @param dataLabel for which to remove data */
  private def _clean(dataLabel: DataLabel) {
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
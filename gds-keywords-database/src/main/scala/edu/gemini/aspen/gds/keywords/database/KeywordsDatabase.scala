package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import edu.gemini.aspen.gds.api.CollectedValue
import collection.mutable.{HashSet, HashMap, Set}

/**
 * Interface for the database
 */
trait KeywordsDatabase {
  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword
   */
  def store(dataLabel:DataLabel, value:CollectedValue)

  /**
   * Retrieve data from the database
   *
   * @param dataLabel from which to retrieve data
   * @param keyword keyword to retrieve
   *
   * @return Option containing the value if it was found in the DB
   */
  def retrieve(dataLabel:DataLabel, keyword:FitsKeyword):Option[CollectedValue]

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  def retrieveAll(dataLabel:DataLabel):Option[Set[CollectedValue]]

}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase{

  val map:HashMap[DataLabel, Set[CollectedValue]] = new HashMap

  override def store(dataLabel:DataLabel, value:CollectedValue) {
    if(!map.contains(dataLabel)){
      map.put(dataLabel,new HashSet[CollectedValue]())
    }
    map.get(dataLabel).get.add(value)
  }

  override def retrieve(dataLabel: DataLabel, keyword: FitsKeyword): Option[CollectedValue] = {
    for{
      set <-  map.get(dataLabel)
      value <- set.find(x => x.keyword==keyword)
    } yield value
  }

  override def retrieveAll(dataLabel: DataLabel): Option[Set[CollectedValue]] = map.get(dataLabel)

  @Validate
  def validate() {println("Validating..." + map)}

  @Invalidate
  def invalidate() {println("Invalidating..." + map)}
}



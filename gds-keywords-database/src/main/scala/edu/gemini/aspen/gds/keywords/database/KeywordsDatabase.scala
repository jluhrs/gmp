package edu.gemini.aspen.gds.keywords.database

import collection.mutable.HashMap
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel

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
  def store(dataLabel:DataLabel, keyword:String, value:AnyRef)

  /**
   * Retrieve data from the database
   *
   * @param dataLabel from which to retrieve data
   * @param keyword keyword to retrieve
   *
   * @return Option containing the value if it was found in the DB
   */
  def retrieve(dataLabel:DataLabel, keyword:String):Option[AnyRef]

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  def retrieveAll(dataLabel:DataLabel):Option[HashMap[String, AnyRef]]

}

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase{

  val map:HashMap[DataLabel, HashMap[String,AnyRef]] = new HashMap

  def store(dataLabel:DataLabel, keyword: String, value: AnyRef) = {
    if(!map.contains(dataLabel)){
      map.put(dataLabel,new HashMap[String, AnyRef]())
    }
    map.get(dataLabel).get.put(keyword,value)
  }

  def retrieve(dataLabel:DataLabel, keyword: String):Option[AnyRef] = {
    val innerMapOption = map.get(dataLabel)
    if(!innerMapOption.isDefined){
      return None
    }
    return innerMapOption.get.get(keyword)
  }

  def retrieveAll(dataLabel: DataLabel): Option[HashMap[String, AnyRef]] = {
    return map.get(dataLabel)
  }

  @Validate
  def validate() = {println(map)}

  @Invalidate
  def invalidate() = {println(map)}
}



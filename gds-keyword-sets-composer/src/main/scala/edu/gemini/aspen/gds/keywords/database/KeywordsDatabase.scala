package edu.gemini.aspen.gds.keywords.database

import collection.mutable.HashMap
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.Dataset

/**
 * Interface for the database
 */
trait KeywordsDatabase {
  /**
   * Store the keyword
   *
   * @param dataSet to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword
   */
  def store(dataSet:Dataset, keyword:String, value:AnyRef)

  /**
   * Retrieve data from the database
   *
   * @param dataSet from which to retrieve data
   * @param keyword keyword to retrieve
   *
   * @return Option containing the value if it was found in the DB
   */
  def retrieve(dataSet:Dataset, keyword:String):Option[AnyRef]

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataSet for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  def retrieveAll(dataSet:Dataset):Option[HashMap[String, AnyRef]]

}

@Component
@Provides(specifications = Array(classOf[KeywordsDatabase]))
@Instantiate
class KeywordsDatabaseImpl extends KeywordsDatabase{

  val map:HashMap[Dataset, HashMap[String,AnyRef]] = new HashMap

  def store(dataSet:Dataset, keyword: String, value: AnyRef) = {
    if(!map.contains(dataSet)){
      map.put(dataSet,new HashMap[String, AnyRef]())
    }
    map.get(dataSet).get.put(keyword,value)
  }

  def retrieve(dataSet:Dataset, keyword: String):Option[AnyRef] = {
    val innerMapOption = map.get(dataSet)
    if(!innerMapOption.isDefined){
      return None
    }
    return innerMapOption.get.get(keyword)
  }

  def retrieveAll(dataSet: Dataset): Option[HashMap[String, AnyRef]] = {
    return map.get(dataSet)
  }

  @Validate
  def validate() = {println(map)}

  @Invalidate
  def invalidate() = {println(map)}
}



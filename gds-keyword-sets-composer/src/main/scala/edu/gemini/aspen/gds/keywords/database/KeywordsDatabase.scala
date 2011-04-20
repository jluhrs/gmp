package edu.gemini.aspen.gds.keywords.database

import collection.mutable.HashMap
import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.Dataset

trait KeywordsDatabase {
  def store(dataSet:Dataset, keyword:String, value:AnyRef)

  def retrieve(dataSet:Dataset, keyword:String):Option[AnyRef]

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

  @Validate
  def validate() = {println(map)}

  @Invalidate
  def invalidate() = {println(map)}
}



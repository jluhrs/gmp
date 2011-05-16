package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import actors.Actor
import collection.JavaConversions._
import edu.gemini.fits.{DefaultHeader, Header, HeaderItem}
import scala.None
import edu.gemini.aspen.gds.api.CollectedValue

/**
 * Interface for the database
 */
trait KeywordsDatabase extends Actor

//case classes define the messages accepted by the DataBase
case class Store(dataLabel: DataLabel, value: CollectedValue)

case class Retrieve(dataLabel: DataLabel, keyword: FitsKeyword)

case class RetrieveExtension(dataLabel: DataLabel, index: Int)

case class RetrieveAll(dataLabel: DataLabel)

//todo: retrieve a specific header? ex. dataset:X, header:0

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {

  start()

  def act() {
    loop {
      react {
        case Store(dataLabel, value) => store(dataLabel, value)
        case Retrieve(dataLabel, keyword) => sender ! retrieve(dataLabel, keyword)
        case RetrieveExtension(dataLabel, index) => sender ! retrieveExtension(dataLabel, index)
        case RetrieveAll(dataLabel) => sender ! retrieveAll(dataLabel)
        case x:Any => throw new RuntimeException("Argument not known " + x)
      }
    }
  }


  private val map = collection.mutable.Map.empty[DataLabel, List[Header]]

  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword
   */
  private def store(dataLabel: DataLabel, value: CollectedValue) {
    if (!map.contains(dataLabel)) {
      map += (dataLabel -> List[Header]())
    }
    val headers: List[Header] = for {
      headerList <- map.get(dataLabel).toList
      header: Header <- headerList
      if header.getIndex == value.index
    } yield header

    for (header <- headers) {
      //should only be one...
      header.add(value) //implicit conversion to HeaderItem
    }
    if (headers.isEmpty) {
      //if couldn't find the header, then add it
      val header = new DefaultHeader(value.index)
      header.add(value) //implicit conversion to HeaderItem
      map.put(dataLabel, header :: map.get(dataLabel).get)
    }
  }

  /**
   * Retrieve data from the database
   *
   * @param dataLabel from which to retrieve data
   * @param keyword keyword to retrieve
   *
   * @return Option containing the value if it was found in the DB
   */
  private def retrieve(dataLabel: DataLabel, keyword: FitsKeyword): Option[HeaderItem] = {
    //todo: should return list?
    val items = for {
      headerList <- map.get(dataLabel).toList
      header <- headerList
      item = header.get(keyword.getName)
      if item != null
    } yield item

    if (items.isEmpty) {
      None
    } else {
      Some(items.get(0))
    }
  }

  /**
   * Retrieve data from the database
   *
   * @param dataLabel from which to retrieve data
   * @param keyword keyword to retrieve
   *
   * @return Option containing the value if it was found in the DB
   */
  private def retrieveExtension(dataLabel: DataLabel, index: Int): Option[Header] = {
    val headers = for {
      headerList <- map.get(dataLabel).toList
      header: Header <- headerList
      if header.getIndex == index
    } yield header

    headers.headOption
  }

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  private def retrieveAll(dataLabel: DataLabel): Option[List[Header]] = map.get(dataLabel)

  @Validate
  def validate() {
    println("Validating..." + map)
  }

  @Invalidate
  def invalidate() {
    println("Invalidating..." + map)
  }
}



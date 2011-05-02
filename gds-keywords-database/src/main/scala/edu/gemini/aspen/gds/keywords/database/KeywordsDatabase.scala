package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import collection.mutable.{HashSet, HashMap, Set}
import actors.Actor
import collection.JavaConversions._
import edu.gemini.fits.{DefaultHeaderItem, DefaultHeader, Header, HeaderItem}
import scala.None

/**
 * Interface for the database
 */
trait KeywordsDatabase extends Actor

//case classes define the messages accepted by the DataBase
case class Store(dataLabel: DataLabel, value: HeaderItem)

case class Retrieve(dataLabel: DataLabel, keyword: FitsKeyword)

case class RetrieveAll(dataLabel: DataLabel)

@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {


  def act() {
    loop {
      react {
        case Store(dataLabel, value) => store(dataLabel, value)
        case Retrieve(dataLabel, keyword) => sender ! retrieve(dataLabel, keyword)
        case RetrieveAll(dataLabel) => sender ! retrieveAll(dataLabel)
        case _ => throw new RuntimeException("Argument not known")
      }
    }
  }


  val map = collection.mutable.Map.empty[DataLabel, List[Header]]

  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword
   */
  private def store(dataLabel: DataLabel, item: HeaderItem) {
    if (!map.contains(dataLabel)) {
      map += (dataLabel -> List[Header]())
    }
    val headerList = map.get(dataLabel).get
    val added: List[Boolean] = for {
      header: Header <- headerList
      //      if header.getIndex == value.getIndex //should be unique
      if true //should be unique
    } yield {
      header.add(item)
    }

    if (added.size == 0) {
      //if couldn't find the header, then add it
      val head = new DefaultHeader()
      head.add(item)
      map.put(dataLabel, List[Header](head))
      //map.put(dataLabel ,(new DefaultHeader(List(item)))::Nil)
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
    if (!map.contains(dataLabel)) {
      None
    } else {
      val items: List[HeaderItem] = for {
        header <- map.get(dataLabel).get;
        item: HeaderItem = header.get(keyword.getName)
        if item != null
      } yield item

      if (items.isEmpty) {
        None
      } else {
        Some(items.get(0))
      }
    }
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



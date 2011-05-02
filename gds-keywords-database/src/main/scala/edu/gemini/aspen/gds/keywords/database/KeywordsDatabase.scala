package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.{FitsKeyword, DataLabel}
import collection.mutable.{HashSet, HashMap, Set}
import actors.Actor
import edu.gemini.fits.HeaderItem

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


  val map: HashMap[DataLabel, Set[HeaderItem]] = new HashMap

  /**
   * Store the keyword
   *
   * @param dataLabel to which the keywords belong
   * @param keyword keyword to store
   * @param value value to associate to the keyword
   */
  private def store(dataLabel: DataLabel, value: HeaderItem) {
    if (!map.contains(dataLabel)) {
      map.put(dataLabel, new HashSet[HeaderItem]())
    }
    map.get(dataLabel).get.add(value)
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
    for {
      set <- map.get(dataLabel)
      value <- set.find(x => x.getKeyword == keyword.getName)
    } yield value
  }

  /**
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  private def retrieveAll(dataLabel: DataLabel): Option[Set[HeaderItem]] = map.get(dataLabel)

  @Validate
  def validate() {
    println("Validating..." + map)
  }

  @Invalidate
  def invalidate() {
    println("Invalidating..." + map)
  }
}



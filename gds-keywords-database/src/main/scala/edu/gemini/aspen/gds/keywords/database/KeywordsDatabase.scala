package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import edu.gemini.fits.{DefaultHeader, Header}
import edu.gemini.aspen.gds.api.{FitsType, CollectedValue}

/**
 * Interface for the database
 */
trait KeywordsDatabase extends Actor

//case classes define the messages accepted by the DataBase
case class Store[T](dataLabel: DataLabel, value: CollectedValue[T])(implicit val _value: FitsType[T])

//helper object to be able to extract implicit parameter from case class while pattern matching
object _Store {
  def unapply(in: Store[_]) = Some(in.dataLabel, in.value, in._value)
}

case class Retrieve(dataLabel: DataLabel)

case class Clean(dataLabel: DataLabel)

/**
 * Component to store CollectedValue as HeaderItem, associated to DataLabel
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {

  start()

  def act() {
    loop {
      react {
        case _Store(dataLabel, value, _value) => _value.getType match {
          case i if i.isAssignableFrom(classOf[Int]) => store(dataLabel, value.asInstanceOf[CollectedValue[Int]])
          case i if i.isAssignableFrom(classOf[Double]) => store(dataLabel, value.asInstanceOf[CollectedValue[Double]])
          case i if i.isAssignableFrom(classOf[String]) => store(dataLabel, value.asInstanceOf[CollectedValue[String]])
        }
        case Retrieve(dataLabel) => sender ! retrieve(dataLabel)
        case Clean(dataLabel) => clean(dataLabel)
        case x: Any => throw new RuntimeException("Argument not known " + x)
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
  private def store[T: FitsType](dataLabel: DataLabel, value: CollectedValue[T]) {
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
   * Retrieve all the data associated to a given data set
   *
   * @param dataLabel for which to retrieve data
   *
   * @return a HashMap[String, AnyRef] containing the data for the given data set
   */
  private def retrieve(dataLabel: DataLabel): Option[List[Header]] = map.get(dataLabel)

  /**
   * Remove all keywords associated with a given DataLabel
   *
   * @param dataLabel for which to remove data
   */
  private def clean(dataLabel: DataLabel) {
    map.remove(dataLabel)
  }

  @Validate
  def validate() {
    println("Validating..." + map)
  }

  @Invalidate
  def invalidate() {
    println("Invalidating..." + map)
  }
}



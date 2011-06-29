package edu.gemini.aspen.gds.keywords.database

import org.apache.felix.ipojo.annotations._
import edu.gemini.aspen.giapi.data.DataLabel
import actors.Actor
import edu.gemini.aspen.gds.api.CollectedValue
import java.util.logging.Logger

/**
 * Interface for the database
 */
trait KeywordsDatabase extends Actor

//case classes define the messages accepted by the DataBase
//store a CollectedValue associated with a data label
case class Store(dataLabel: DataLabel, value: CollectedValue[_])

//retrieve all CollectedValues associated with a data label
case class Retrieve(dataLabel: DataLabel)

//remove all the CollectedValues associated with a data label
case class Clean(dataLabel: DataLabel)

/**
 * Component to store CollectedValue as HeaderItem, associated to DataLabel
 */
@Component
@Instantiate
@Provides(specifications = Array(classOf[KeywordsDatabase]))
class KeywordsDatabaseImpl extends KeywordsDatabase {
    private val LOG = Logger.getLogger(this.getClass.getName)

    start()

    def act() {
        loop {
            react {
                case Store(dataLabel: DataLabel, value: CollectedValue[_]) => store(dataLabel, value)
                case Retrieve(dataLabel) => reply(retrieve(dataLabel))
                case Clean(dataLabel) => clean(dataLabel)
                case x: Any => throw new RuntimeException("Argument not known " + x)
            }
        }
    }

    private val map = collection.mutable.Map.empty[DataLabel, List[CollectedValue[_]]]

    /**
     * Store the keyword
     *
     * @param dataLabel to which the keywords belong
     * @param keyword keyword to store
     * @param value value to associate to the keyword
     */
    private def store(dataLabel: DataLabel, headerItem: CollectedValue[_]) {
        if (!map.contains(dataLabel)) {
            map += (dataLabel -> List[CollectedValue[_]]())
        }
        map.put(dataLabel, headerItem :: map.getOrElse(dataLabel, List[CollectedValue[_]]()))
        /*val headers: List[CollectedValue[_]] = for {
            headerList <- map.get(dataLabel).toList
            header: Header <- headerList
            if header.getIndex == index
        } yield header

        for (header <- headers) {
            //should only be one...
            header.add(headerItem) //implicit conversion to HeaderItem
        }
        if (headers.isEmpty) {
            //if couldn't find the header, then add it
            val header = new DefaultHeader(index)
            header.add(headerItem) //implicit conversion to HeaderItem
            map.put(dataLabel, header :: map.get(dataLabel).get)
        }*/
    }

    /**
     * Retrieve all the data associated to a given data set
     *
     * @param dataLabel for which to retrieve data
     *
     * @return a HashMap[String, AnyRef] containing the data for the given data set
     */
    private def retrieve(dataLabel: DataLabel): Option[List[CollectedValue[_]]] = map.get(dataLabel)

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
        LOG.info("Validating KeywordsDatabase")
    }

    @Invalidate
    def invalidate() {
        LOG.info("Invalidating KeywordsDatabase")
    }
}



package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import java.util.logging.Logger
import edu.gemini.aspen.gds.api._

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    private val LOG = Logger.getLogger(this.getClass.getName)

    override def collectValues(): List[CollectedValue[_]] = {
        val readValue = Option(epicsReader.getValue(sourceChannel))
        try {
            readValue map (convertCollectedValue) orElse (defaultCollectedValue) toList
        } catch {
            case e: MatchError => {
                LOG.warning("Data for " + fitsKeyword + " keyword was not of the type specified in config file.")
                List(ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex))
            }
        }
    }

    def convertCollectedValue(epicsValue: AnyRef): CollectedValue[_] = {
        dataType match {
            case DataType("STRING") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[_]](arrayIndex).toString, fitsComment, headerIndex)
            case DataType("DOUBLE") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](arrayIndex), fitsComment, headerIndex)
            case DataType("INT") => CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Int]](arrayIndex), fitsComment, headerIndex)
            // todo, this should not happen since tha parser limits it
            case _ => ErrorCollectedValue(fitsKeyword, CollectionError.TypeMismatch, fitsComment, headerIndex)
        }
    }
}

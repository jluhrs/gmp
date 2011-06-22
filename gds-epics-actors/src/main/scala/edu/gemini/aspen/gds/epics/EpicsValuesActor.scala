package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import java.util.logging.Logger
import edu.gemini.aspen.gds.api._

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {

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
        val valueArray = epicsValue.asInstanceOf[Array[_]]
        if (arrayIndex < valueArray.length) {
            valueToCollectedValue(valueArray(arrayIndex))
        } else {
            ErrorCollectedValue(fitsKeyword, CollectionError.ArrayIndexOutOfBounds, fitsComment, headerIndex)
        }
    }
}

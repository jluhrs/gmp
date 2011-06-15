package edu.gemini.aspen.gds.epics

import edu.gemini.epics.EpicsReader
import edu.gemini.aspen.gds.api.{OneItemKeywordValueActor, GDSConfiguration, CollectedValue}

/**
 * Very simple actor that can produce as a reply of a Collect request a single value
 * linked to a single fitsKeyword
 */
class EpicsValuesActor(epicsReader: EpicsReader, configuration: GDSConfiguration) extends OneItemKeywordValueActor(configuration) {
    override def collectValues(): List[CollectedValue[_]] = {
        val readValue = Option(epicsReader.getValue(sourceChannel))
        
        readValue map (collectedValue) orElse (defaultCollectedValue) toList
    }

    def collectedValue(epicsValue: AnyRef): CollectedValue[_] = {
        //todo: add support for other data types
        if (epicsValue.isInstanceOf[Array[Double]]) {
            // TODO: This should be done on the EpicsArray Actor
            CollectedValue(fitsKeyword, epicsValue.asInstanceOf[Array[Double]](0), fitsComment, headerIndex)
        } else if (epicsValue.isInstanceOf[String]) {
            // TODO: This should be done on the EpicsArray Actor
            CollectedValue(fitsKeyword, epicsValue.asInstanceOf[String], fitsComment, headerIndex)
        } else {
            null
        }
    }

}
